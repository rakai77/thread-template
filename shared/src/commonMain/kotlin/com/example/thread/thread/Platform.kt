package com.example.thread.thread

import io.ktor.client.HttpClient

interface Platform {
    val name: String
    fun getHttpClient(isFromMultipart: Boolean): HttpClient
}

expect fun getPlatform(): Platform