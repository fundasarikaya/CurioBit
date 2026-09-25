package com.genelkultur.app.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

actual fun createHttpClient(): HttpClient = HttpClient(Android) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true })
    }
}
