package com.example.thread.thread

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngineFactory

interface Platform {
    val name: String
    fun getHttpClient(isFromMultipart: Boolean): HttpClient
}

expect fun getPlatform(): Platform