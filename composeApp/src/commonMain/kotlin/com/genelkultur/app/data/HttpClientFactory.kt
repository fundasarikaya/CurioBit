package com.genelkultur.app.data

import io.ktor.client.HttpClient

/**
 * Platforma özel HTTP engine ile bir Ktor istemcisi oluşturur. Ktor'un motoru
 * otomatik seçmesine (`HttpClient { ... }`) güvenmek yerine engine'i açıkça
 * belirtiyoruz; aksi halde iOS'ta (Kotlin/Native) uygulama açılışta çöküyordu.
 */
expect fun createHttpClient(): HttpClient
