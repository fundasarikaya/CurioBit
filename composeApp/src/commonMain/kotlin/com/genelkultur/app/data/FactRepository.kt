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

    suspend fun getFactById(id: String): Fact? = withContext(Dispatchers.Default) {
        queries.selectById(id).executeAsOneOrNull()?.toFact()
    }

    /**
     * Bugün için, kullanıcının daha önce görmediği bir genel kültür bilgisi seçer,
     * kaydeder ve döner. Wikipedia'ya ulaşılamazsa null döner.
     */
    suspend fun fetchAndPickTodaysFact(): Fact? = withContext(Dispatchers.Default) {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val events = try {
            api.fetchOnThisDay(today.monthNumber, today.dayOfMonth)
        } catch (e: Exception) {
            return@withContext null
        }
        if (events.isEmpty()) return@withContext null

        val seenIds = queries.selectAllIds().executeAsList().toSet()

        val candidates = events.mapNotNull { event -> event.toFactOrNull(today.toEpochDays().toLong()) }
        val unseen = candidates.filter { it.id !in seenIds }
        val chosen = (unseen.ifEmpty { candidates }).randomOrNull() ?: return@withContext null

        queries.insertFact(
            id = chosen.id,
            title = chosen.title,
            shortText = chosen.shortText,
            fullText = chosen.fullText,
            sourceUrl = chosen.sourceUrl,
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
        val sourceUrl = page?.content_urls?.desktop?.page
            ?: page?.let { "https://tr.wikipedia.org/wiki/${it.title}" }
            ?: "https://tr.wikipedia.org/wiki/Vikipedi:Bug%C3%BCn"
        return Fact(
            id = "$shownDateEpochDay-$text-$year".hashCode().toString(),
            title = displayTitle,
            shortText = fullText.truncateTo(SHORT_TEXT_MAX_CHARS),
            fullText = fullText,
            sourceUrl = sourceUrl,
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
        sourceUrl = sourceUrl,
        shownDateEpochDay = shownDateEpochDay
    )
}
