package com.example.thread.thread.utils

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


fun setupHttpClient(
    baseUrl: String,
    isDebugMode: Boolean = false,
    httpClientProvider: HttpClient
): HttpClient {

    return httpClientProvider.config {
        ContentEncoding()

        expectSuccess = true

        install(HttpTimeout) {
            this.requestTimeoutMillis = 60_000
            this.connectTimeoutMillis = 60_000
            this.socketTimeoutMillis = 60_000
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
            checkHttpMethod = true
        }

//        install(Auth) {
//            bearer {
//                loadTokens {
//                    BearerTokens(
//                        "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI2MDRkMDZjNjA1YTI3OTRjZjc0ZWFmODUyMGJhNTFiZCIsIm5iZiI6MTczNzM2ODgyOS43NTgwMDAxLCJzdWIiOiI2NzhlMjRmZDQyZjI3Yzc1NGM2NTQ5MmIiLCJzY29wZXMiOlsiYXBpX3JlYWQiXSwidmVyc2lvbiI6MX0.53FQqZAmj1XFy6WKvVlsPsVYd2NT6R2n_euvB6IfsDY",
//                        ""
//                    )
//                }
//                sendWithoutRequest { true }
//            }
//            parameters {
//                append("api_key", "604d06c605a2794cf74eaf8520ba51bd")
//            }
//        }


        defaultRequest {
            host = baseUrl

            url {
                this.user
                protocol = URLProtocol.HTTPS
            }
        }

        if (isDebugMode) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
    }
}