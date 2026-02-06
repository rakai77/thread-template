package com.example.thread.thread.data.remote.websockets

import com.example.thread.thread.data.Result
import com.example.thread.thread.data.remote.response.SpotTickItemResponse
import com.example.thread.thread.data.remote.response.WebSocketMessage
import com.example.thread.thread.data.remote.response.WebSocketSubscribe
import com.example.thread.thread.domain.model.ApiException
import com.example.thread.thread.domain.model.toException
import com.example.thread.thread.utils.Constants
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class CryptoWebSocketService(
    private val httpClient: HttpClient,
    private val json: Json
) {
    private var currentSession: DefaultClientWebSocketSession? = null

    fun observeSpotTicks(
        subscriptions: List<String>
    ): Flow<Result<SpotTickItemResponse>> = flow {
        try {
            httpClient.webSocket(
                host = "data-streamer.coindesk.com",
                path = Constants.WS_SPOT_LATEST_TICK_UNMAPPED,
                request = {
                    header("authorization", "Apikey ${Constants.API_KEY}")
                }
            ) {
                currentSession = this

                // Send subscription request
                val subscribeMessage = WebSocketSubscribe(
                    action = "SubAdd",
                    subscriptions = subscriptions
                )
                send(Frame.Text(json.encodeToString(subscribeMessage)))

                // Listen for incoming messages
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val text = frame.readText()

                        try {
                            val message = json.decodeFromString<WebSocketMessage>(text)

                            // Handle different message types
                            when (message.type) {
                                "20" -> { // Heartbeat
                                    // Optional: Log heartbeat
                                }
                                "2" -> { // Error
                                    val error = message.error?.toException()
                                        ?: ApiException.WebSocketConnectionError(
                                            message.message ?: "WebSocket error"
                                        )
                                    emit(Result.Error(error, error.message))
                                }
                                "5" -> { // Data update
                                    message.data?.let { tickData ->
                                        emit(Result.Success(tickData))
                                    }
                                }
                                "17" -> { // Subscription success
                                    // Optional: Log subscription success
                                }
                                "16" -> { // Subscription error
                                    val error = ApiException.WebSocketAuthError(
                                        message.message ?: "Subscription failed"
                                    )
                                    emit(Result.Error(error, error.message))
                                }
                                else -> {
                                    // Handle other message types
                                }
                            }
                        } catch (e: Exception) {
                            val error = ApiException.ParsingError(
                                "Failed to parse WebSocket message: ${e.message}",
                                e
                            )
                            emit(Result.Error(error, error.message))
                        }
                    } else if (frame is Frame.Close) {
                        val error = ApiException.WebSocketConnectionError(
                            "WebSocket connection closed"
                        )
                        emit(Result.Error(error, error.message))
                        break
                    }
                }
            }
        } catch (e: Exception) {
            val error = when (e) {
                is ApiException -> e
                else -> ApiException.WebSocketConnectionError(
                    "WebSocket connection failed: ${e.message}",
                    e
                )
            }
            emit(Result.Error(error, error.message))
        }
    }.catch { e ->
        val error = e as? ApiException ?: ApiException.UnknownError(e.message ?: "Unknown error", e)
        emit(Result.Error(error, error.message))
    }

    suspend fun closeConnection() {
        currentSession?.close(CloseReason(CloseReason.Codes.NORMAL, "Client closed connection"))
        currentSession = null
        httpClient.close()
    }

    suspend fun unsubscribe(subscriptions: List<String>) {
        currentSession?.let { session ->
            val unsubMessage = WebSocketSubscribe(
                action = "SubRemove",
                subscriptions = subscriptions
            )
            session.send(Frame.Text(json.encodeToString(unsubMessage)))
        }
    }
}