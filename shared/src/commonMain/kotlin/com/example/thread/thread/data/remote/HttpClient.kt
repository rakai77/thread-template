package com.example.thread.thread.data.remote

import com.example.thread.thread.utils.Constant
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun setupHttpClient(
    baseUrl: String,
    isDebugMode: Boolean = false,
    httpClientProvider: HttpClient
): HttpClient {
    return httpClientProvider.config {
        expectSuccess = false

        install(WebSockets) {
            pingIntervalMillis = 20_000
            maxFrameSize = Long.MAX_VALUE
            contentConverter = KotlinxWebsocketSerializationConverter(
                createJsonParser()
            )
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 60_000
        }

        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                prettyPrint = true
                useAlternativeNames = false
                explicitNulls = false
            })
        }

        install(HttpRedirect) {
            checkHttpMethod = false
            allowHttpsDowngrade = false
        }

        defaultRequest {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
                parameters.append("api_key", Constant.API_KEY)
            }

            header("Accept", "application/json")
            header("Content-Type", "application/json")
        }

        if (isDebugMode) {
            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Ktor: $message")
                    }
                }
                level = LogLevel.ALL
            }
        }
    }
}

fun createJsonParser(): Json {
    return Json {
        isLenient = true
        ignoreUnknownKeys = true
        prettyPrint = true
        useAlternativeNames = false
        explicitNulls = false
    }
}
