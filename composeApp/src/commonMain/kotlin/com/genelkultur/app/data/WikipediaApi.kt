package com.genelkultur.app.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class WikipediaApi(
    private val httpClient: HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
) {
    /**
     * Fetches "bugün" (On This Day) events for the given month/day from the
     * Turkish Wikipedia REST API.
     */
    suspend fun fetchOnThisDay(month: Int, day: Int): List<OnThisDayEvent> {
        val mm = month.toString().padStart(2, '0')
        val dd = day.toString().padStart(2, '0')
        val response: OnThisDayResponse = httpClient
            .get("https://tr.wikipedia.org/api/rest_v1/feed/onthisday/events/$mm/$dd")
            .body()
        return response.events
    }
}
