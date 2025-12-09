package com.example.thread.thread.data.remote.websocket

import com.example.thread.thread.utils.Constant
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readReason
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

interface WebSocketClient{

    fun <T> observeMessages(
        host: String,
        path: String,
        parser: (String) -> T?,
        onConnect: suspend DefaultClientWebSocketSession.() -> Unit = {}
    ) : Flow<Result<T>>

    suspend fun sendMessage(path: String, message: String)
    suspend fun closeConnection()
}

class WebSocketClientImpl(
    private val client: HttpClient,
    private val json: Json
) : WebSocketClient {

    private var session: DefaultClientWebSocketSession? = null
    private val _isConnected = MutableStateFlow(false)
    val isConnected = _isConnected.asStateFlow()

    override fun <T> observeMessages(
        host: String,
        path: String,
        parser: (String) -> T?,
        onConnect: suspend DefaultClientWebSocketSession.() -> Unit
    ): Flow<Result<T>> {
        return flow {
            try {
                // ✅ FIX: Correct WebSocket URL format with v2 and API key
                val wsUrl = "$path?api_key=${Constant.API_KEY}"
                println("🔌 Connecting to WebSocket: wss://$host$wsUrl")

                client.webSocket(
                    method = HttpMethod.Get,
                    host = host,
                    path = wsUrl,
                    request = {
                        url {
                            protocol = URLProtocol.WSS
                        }
                    }
                ) {
                    session = this
                    _isConnected.value = true

                    println("✅ WebSocket connected successfully")

                    // Execute connection callback (for subscribing)
                    try {
                        onConnect()
                        println("📡 Subscription sent successfully")
                    } catch (e: Exception) {
                        println("❌ Subscription error: ${e.message}")
                        emit(Result.failure(e))
                        return@webSocket
                    }

                    // Listen for incoming messages
                    try {
                        for (frame in incoming) {
                            when (frame) {
                                is Frame.Text -> {
                                    val text = frame.readText()
                                    println("📥 Received message: $text")

                                    try {
                                        parser(text)?.let {
                                            emit(Result.success(it))
                                            println("✅ Message parsed successfully")
                                        }
                                    } catch (e: Exception) {
                                        println("⚠️ Parser error: ${e.message}")
                                        // Don't fail the entire flow for parse errors
                                    }
                                }
                                is Frame.Close -> {
                                    val reason = frame.readReason()
                                    println("🔌 WebSocket closed: ${reason?.message}")
                                    emit(Result.failure(
                                        Exception("WebSocket closed: ${reason?.message}")
                                    ))
                                    break
                                }
                                else -> {
                                    println("ℹ️ Other frame type: ${frame.frameType}")
                                }
                            }
                        }
                    } catch (e: Exception) {
                        println("❌ Error reading frames: ${e.message}")
                        e.printStackTrace()
                        emit(Result.failure(e))
                    }
                }
            } catch (e: Exception) {
                println("❌ WebSocket connection error: ${e.message}")
                e.printStackTrace()
                emit(Result.failure(e))
            } finally {
                session = null
                _isConnected.value = false
                println("🔌 WebSocket disconnected")
            }
        }.catch { error ->
            println("❌ Flow error: ${error.message}")
            emit(Result.failure(error as? Exception ?: Exception(error.message)))
        }
    }

    override suspend fun sendMessage(path: String, message: String) {
        try {
            session?.send(Frame.Text(message))
            println("📤 Sent message: $message")
        } catch (e: Exception) {
            println("❌ Send error: ${e.message}")
            throw e
        }
    }

    override suspend fun closeConnection() {
        try {
            session?.close(CloseReason(CloseReason.Codes.NORMAL, "Client closed"))
            session = null
            _isConnected.value = false
            println("🔌 Connection closed manually")
        } catch (e: Exception) {
            println("❌ Close error: ${e.message}")
        }
    }
}
