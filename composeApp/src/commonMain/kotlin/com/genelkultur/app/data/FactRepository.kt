package com.genelkultur.app.data

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.genelkultur.app.db.AppDatabase
import com.genelkultur.app.db.SeenFact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** Bir bildirimde gösterilecek kısa metnin karakter sınırı. */
private const val SHORT_TEXT_MAX_CHARS = 140
private const val TITLE_MAX_CHARS = 48
/** Detay ekranında gösterilen uzun anlatımın karakter sınırı. */
private const val LONG_TEXT_MAX_CHARS = 600
private const val HISTORY_WINDOW_DAYS = 7
private const val ERA_KEY = "era"
private val KIND_WEIGHTS = mapOf(EntryKind.EVENT to 2, EntryKind.BIRTH to 1, EntryKind.DEATH to 1)

class FactRepository(
    driverFactory: DatabaseDriverFactory,
    private val api: WikipediaApi = WikipediaApi()
) {
    private val database = AppDatabase(driverFactory.createDriver())
    private val queries = database.factQueries
    private val settings = database.settingQueries

    /** Son 7 günde kullanıcıya gösterilmiş bilgiler, tarihe göre en yeniden eskiye. */
    fun observeRecentFacts(): Flow<List<Fact>> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val sinceEpochDay = today.toEpochDays() - (HISTORY_WINDOW_DAYS - 1)
        return queries.selectRecent(sinceEpochDay.toLong())
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toFact() } }
    }

    /** Kullanıcının beğendiği (LIKE) tüm bilgiler, zaman sınırı olmadan. */
    fun observeFavoriteFacts(): Flow<List<Fact>> {
        return queries.selectFavorites()
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { rows -> rows.map { it.toFact() } }
    }

    suspend fun getFactById(id: String): Fact? = withContext(Dispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toFact()
    }

    /**
     * Bir bilgiye kullanıcı tepkisini kaydeder. "Dislike" edilen bilginin konusu,
     * gelecekteki seçimlerde öncelik dışına atılması için ayrıca saklanır; tepki
     * kaldırılırsa (NONE/LIKE) o konu tekrar normal önceliğe döner.
     */
    suspend fun setReaction(id: String, reaction: Reaction) = withContext(Dispatchers.Default) {
        val fact = queries.selectById(id).executeAsOneOrNull() ?: return@withContext
        queries.updateReaction(reaction.name, id)
        if (reaction == Reaction.DISLIKE) {
            queries.addAvoidedTopic(fact.topic)
        } else {
            queries.removeAvoidedTopic(fact.topic)
        }
    }

    /**
     * Bugün zaten bir bilgi gösterildiyse (uygulamada ya da bildirimde) en sonuncusunu
     * döner; böylece uygulama her açıldığında manşet değişmez. Yoksa yeni bir tane seçer.
     */
    suspend fun getOrPickTodaysFact(): Fact? = withContext(Dispatchers.Default) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        queries.selectLatestForDay(today.toEpochDays().toLong()).executeAsOneOrNull()?.toFact()
            ?: fetchAndPickTodaysFact()
    }

    /** Kullanıcının seçtiği dönem; seçim yapılmadıysa tüm yıllar. */
    suspend fun getEra(): Era = withContext(Dispatchers.Default) {
        val saved = settings.selectSetting(ERA_KEY).executeAsOneOrNull()
        Era.entries.firstOrNull { it.name == saved } ?: Era.ALL
    }

    suspend fun setEra(era: Era) = withContext(Dispatchers.Default) {
        settings.upsertSetting(ERA_KEY, era.name)
    }

    /**
     * Bugün için, kullanıcının daha önce görmediği bir bilgi seçer, kaydeder ve döner.
     * Uygulama açılışında ve bildirimlerde kullanılır: seçilen dönemde bugüne ait bilgi
     * yoksa boş kalmamak için başka bir dönemden seçer. Vikipedi'ye ulaşılamazsa null.
     */
    suspend fun fetchAndPickTodaysFact(): Fact? =
        (pick(requireUnseen = false, strictEra = false) as? PickResult.Picked)?.fact

    /**
     * Kullanıcı "başka bir bilgi" istediğinde: yalnızca seçilen dönemden ve daha önce
     * gösterilmemiş bir bilgi seçer.
     */
    suspend fun pickAnotherFact(): PickResult = pick(requireUnseen = true, strictEra = true)

    /**
     * Dönem değiştiğinde: seçilen dönemden bir bilgi getirir; gösterilmemiş kalmadıysa
     * o dönemden daha önce gösterilmiş olanı tekrar gösterebilir.
     */
    suspend fun pickForEra(): PickResult = pick(requireUnseen = false, strictEra = true)

    private suspend fun pick(requireUnseen: Boolean, strictEra: Boolean): PickResult =
        withContext(Dispatchers.Default) {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            val response = try {
                api.fetchOnThisDay(today.monthNumber, today.dayOfMonth)
            } catch (e: Exception) {
                return@withContext PickResult.Offline
            }
            val era = getEra()
            val seenIds = queries.selectAllIds().executeAsList().toSet()
            val avoidedTopics = queries.selectAvoidedTopics().executeAsList().toSet()

            val day = today.toEpochDays().toLong()
            val entries = response.events.map { EntryKind.EVENT to it } +
                response.births.map { EntryKind.BIRTH to it } +
                response.deaths.map { EntryKind.DEATH to it }
            val candidates = entries.mapNotNull { (kind, entry) ->
                entry.toFactOrNull(day, kind)?.let { kind to it }
            }
            val unseen = candidates.filter { (_, fact) -> fact.id !in seenIds }
            val pool = when {
                unseen.isNotEmpty() -> unseen
                requireUnseen -> return@withContext PickResult.NoneLeft
                else -> candidates
            }
            // Önce seçilen dönemdekiler; hiç yoksa ya dur (strictEra) ya da tüm dönemlere dön.
            val inEra = pool.filter { (_, fact) -> era.contains(yearOf(fact.fullText)) }
            val eraPool = when {
                inEra.isNotEmpty() -> inEra
                strictEra -> return@withContext PickResult.NoneLeft
                else -> pool
            }
            // Doğumlar olaylardan çok daha fazla; önce türü ağırlıkla seçip sonra o türden
            // seçiyoruz ki uygulama bir doğum günü listesine dönmesin.
            val byKind = eraPool.groupBy({ it.first }, { it.second })
            val kind = pickWeighted(byKind.keys) ?: return@withContext PickResult.NoneLeft
            // "Dislike" edilen konularla eşleşenler tamamen elenmez, sadece öncelik dışına atılır.
            val (preferred, deprioritized) = byKind.getValue(kind).partition { it.topic !in avoidedTopics }
            val chosen = (preferred.ifEmpty { deprioritized }).randomOrNull()
                ?: return@withContext PickResult.NoneLeft

            queries.insertFact(
                id = chosen.id,
                title = chosen.title,
                shortText = chosen.shortText,
                fullText = chosen.fullText,
                longText = chosen.longText,
                sourceUrl = chosen.sourceUrl,
                topic = chosen.topic,
                shownDateEpochDay = chosen.shownDateEpochDay
            )
            PickResult.Picked(chosen)
        }

    suspend fun cleanupOlderThan(epochDay: Long) = withContext(Dispatchers.Default) {
        queries.deleteOlderThan(epochDay)
    }

    /** Olaylar 2, doğumlar ve ölümler 1 ağırlıkla; yalnızca elde bulunan türler arasından. */
    private fun pickWeighted(kinds: Set<EntryKind>): EntryKind? {
        val weighted = kinds.flatMap { kind -> List(KIND_WEIGHTS.getValue(kind)) { kind } }
        return weighted.randomOrNull()
    }

    private fun OnThisDayEvent.toFactOrNull(shownDateEpochDay: Long, kind: EntryKind): Fact? {
        if (text.isBlank()) return null
        val page = pages.firstOrNull()
        val year = year?.toString().orEmpty()
        val text = when (kind) {
            EntryKind.EVENT -> text
            EntryKind.BIRTH -> personSentence(text, "doğdu")
            EntryKind.DEATH -> personSentence(text, "hayatını kaybetti")
        }
        val fullText = if (year.isNotEmpty()) "$year — $text" else text
        // Başlık, olayla alakasız olabilen bir Wikipedia sayfa adı yerine
        // olayın kendi metninden türetilir; böylece her zaman konuyla ilgili olur.
        val displayTitle = text.truncateTo(TITLE_MAX_CHARS)
        val topic = page?.title?.replace('_', ' ') ?: displayTitle
        val sourceUrl = page?.content_urls?.desktop?.page
            ?: page?.let { "https://tr.wikipedia.org/wiki/${it.title}" }
            ?: "https://tr.wikipedia.org/wiki/Vikipedi:Bug%C3%BCn"
        // Detay ekranında bildirimdeki tek cümleden biraz daha fazlasını göstermek için
        // Wikipedia sayfasının özetini (varsa) kullanıyoruz; tam makale kaynak linkinde.
        val longText = page?.extract?.takeIf { it.isNotBlank() }?.truncateTo(LONG_TEXT_MAX_CHARS) ?: fullText
        return Fact(
            // Olayların kimliği eski biçimde kalır ki daha önce gösterilenler tekrar gelmesin.
            id = when (kind) {
                EntryKind.EVENT -> "$shownDateEpochDay-$text-$year"
                else -> "$shownDateEpochDay-$kind-$text-$year"
            }.hashCode().toString(),
            title = displayTitle,
            shortText = fullText.truncateTo(SHORT_TEXT_MAX_CHARS),
            fullText = fullText,
            longText = longText,
            sourceUrl = sourceUrl,
            topic = topic,
            shownDateEpochDay = shownDateEpochDay
        )
    }

    /**
     * Vikipedi'nin "İsim, açıklama (d. 1900)" biçimindeki doğum/ölüm kaydını cümleye
     * çevirir: "İsim doğdu: açıklama (d. 1900)". Açıklamalar tutarsız olduğundan
     * ("… devlet adamıydı" gibi) ismi başa alan tek bir kalıp kullanılır.
     */
    private fun personSentence(raw: String, verb: String): String {
        val text = raw.replace('\u00A0', ' ').trim()
        val comma = text.indexOf(", ")
        if (comma <= 0) return "$text $verb."
        val name = text.substring(0, comma).trim()
        val description = text.substring(comma + 2).trim()
        return "$name $verb: $description"
    }

    private fun String.truncateTo(maxChars: Int): String {
        if (length <= maxChars) return this
        val cut = substring(0, maxChars)
        val lastSpace = cut.lastIndexOf(' ')
        val trimmed = if (lastSpace > 0) cut.substring(0, lastSpace) else cut
        return "$trimmed…"
    }

    private fun SeenFact.toFact() = Fact(
        id = id,
        title = title,
        shortText = shortText,
        fullText = fullText,
        longText = longText,
        sourceUrl = sourceUrl,
        topic = topic,
        reaction = runCatching { Reaction.valueOf(reaction) }.getOrDefault(Reaction.NONE),
        shownDateEpochDay = shownDateEpochDay
    )
}
