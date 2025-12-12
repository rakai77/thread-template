package com.example.thread.thread.data.remote

import com.example.thread.thread.domain.model.ApiException
import com.example.thread.thread.utils.Constants
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.pingInterval
import io.ktor.client.request.header
import io.ktor.http.*
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


fun setupHttpClient(
    baseUrl: String,
    isDebugMode: Boolean = false,
    httpClientProvider: HttpClient
): HttpClient {
    return httpClientProvider.config {
        expectSuccess = true

        install(HttpTimeout) {
            requestTimeoutMillis = Constants.REQUEST_TIMEOUT
            connectTimeoutMillis = Constants.REQUEST_TIMEOUT
            socketTimeoutMillis = Constants.REQUEST_TIMEOUT
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
            allowHttpsDowngrade = false
        }

        defaultRequest {
            host = baseUrl
            url {
                protocol = URLProtocol.HTTPS
            }
            header("authorization", "Apikey ${Constants.API_KEY}")
            header("Content-Type", "application/json")
        }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { exception, _ ->
                val clientException = when (exception) {
                    is HttpRequestTimeoutException ->
                        ApiException.TimeoutError(cause = exception)
                    is ResponseException -> {
                        when (exception.response.status.value) {
                            401, 403 -> ApiException.AuthenticationError(cause = exception)
                            404 -> ApiException.DataNotFoundError(cause = exception)
                            429 -> ApiException.RateLimitError(cause = exception)
                            in 500..599 -> ApiException.ServerError(
                                exception.response.status.value,
                                cause = exception
                            )
                            else -> ApiException.UnknownError(
                                exception.message ?: "Unknown error",
                                exception
                            )
                        }
                    }
                    else -> ApiException.NetworkError(
                        exception.message ?: "Network error",
                        exception
                    )
                }
                throw clientException
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

fun setupWebSocketClient(
    isDebugMode: Boolean = false,
    httpClientProvider: HttpClient
): HttpClient {
    return httpClientProvider.config {
        install(WebSockets) {
            pingIntervalMillis = Constants.WEBSOCKET_PING_INTERVAL
            contentConverter = KotlinxWebsocketSerializationConverter(Json {
                isLenient = true
                ignoreUnknownKeys = true
                prettyPrint = true
                explicitNulls = false
            })
        }

        install(HttpTimeout) {
            requestTimeoutMillis = null // No timeout for WebSocket
            socketTimeoutMillis = null
        }

        if (isDebugMode) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
        }
    }
}