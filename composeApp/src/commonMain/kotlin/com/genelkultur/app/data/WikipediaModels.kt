package com.genelkultur.app.data

import kotlinx.serialization.Serializable

/**
 * tr.wikipedia.org REST API — /api/rest_v1/feed/onthisday/events/{mm}/{dd}
 */
@Serializable
data class OnThisDayResponse(
    val events: List<OnThisDayEvent> = emptyList()
)

@Serializable
data class OnThisDayEvent(
    val text: String,
    val year: Int? = null,
    val pages: List<WikiPage> = emptyList()
)

@Serializable
data class WikiPage(
    val title: String,
    val extract: String? = null,
    val content_urls: ContentUrls? = null
)

@Serializable
data class ContentUrls(
    val desktop: DesktopUrl? = null
)

@Serializable
data class DesktopUrl(
    val page: String? = null
)

/** Kullanıcının bir bilgiye verdiği yerel tepki. */
enum class Reaction {
    NONE, LIKE, DISLIKE
}

/** Domain model used across the app, independent of the Wikipedia response shape. */
data class Fact(
    val id: String,
    val title: String,
    val shortText: String,
    val fullText: String,
    /** Detay sayfasında gösterilen, Wikipedia sayfa özetinden gelen daha uzun anlatım. */
    val longText: String,
    val sourceUrl: String,
    /** Bağlı olduğu Wikipedia sayfasının konusu; "dislike" sonrası benzer konuları
     *  önceliklendirmek için kullanılır, kullanıcıya gösterilmez. */
    val topic: String,
    val reaction: Reaction = Reaction.NONE,
    val shownDateEpochDay: Long
)
