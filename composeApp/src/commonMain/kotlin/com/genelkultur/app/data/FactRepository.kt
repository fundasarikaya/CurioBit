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

class FactRepository(
    driverFactory: DatabaseDriverFactory,
    private val api: WikipediaApi = WikipediaApi()
) {
    private val database = AppDatabase(driverFactory.createDriver())
    private val queries = database.factQueries

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

    /**
     * Bugün için, kullanıcının daha önce görmediği bir genel kültür bilgisi seçer,
     * kaydeder ve döner. Wikipedia'ya ulaşılamazsa null döner. [requireUnseen] true
     * ise ve bugünün tüm bilgileri görülmüşse eskisini tekrarlamak yerine null döner.
     */
    suspend fun fetchAndPickTodaysFact(requireUnseen: Boolean = false): Fact? = withContext(Dispatchers.Default) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val events = try {
            api.fetchOnThisDay(today.monthNumber, today.dayOfMonth)
        } catch (e: Exception) {
            return@withContext null
        }
        if (events.isEmpty()) return@withContext null

        val seenIds = queries.selectAllIds().executeAsList().toSet()
        val avoidedTopics = queries.selectAvoidedTopics().executeAsList().toSet()

        val candidates = events.mapNotNull { event -> event.toFactOrNull(today.toEpochDays().toLong()) }
        val unseen = candidates.filter { it.id !in seenIds }
        if (requireUnseen && unseen.isEmpty()) return@withContext null
        // "Dislike" edilen konularla eşleşenler tamamen elenmez, sadece öncelik dışına atılır.
        val (preferred, deprioritized) = (unseen.ifEmpty { candidates })
            .partition { it.topic !in avoidedTopics }
        val chosen = (preferred.ifEmpty { deprioritized }).randomOrNull() ?: return@withContext null

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
        chosen
    }

    suspend fun cleanupOlderThan(epochDay: Long) = withContext(Dispatchers.Default) {
        queries.deleteOlderThan(epochDay)
    }

    private fun OnThisDayEvent.toFactOrNull(shownDateEpochDay: Long): Fact? {
        if (text.isBlank()) return null
        val page = pages.firstOrNull()
        val year = year?.toString().orEmpty()
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
            id = "$shownDateEpochDay-$text-$year".hashCode().toString(),
            title = displayTitle,
            shortText = fullText.truncateTo(SHORT_TEXT_MAX_CHARS),
            fullText = fullText,
            longText = longText,
            sourceUrl = sourceUrl,
            topic = topic,
            shownDateEpochDay = shownDateEpochDay
        )
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
