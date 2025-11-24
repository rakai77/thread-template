package com.example.thread.thread.data.remote.websocket

import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json

interface WebSocketClient{

    fun <T> observeMessages(
        path: String,
        parser: (String) -> T?,
    ) : Flow<Result<T>>

    suspend fun sendMessage(path: String, message: String)
    suspend fun closeConnection()
}

class WebSocketClientImpl(
    private val client: HttpClient,
    private val baseUrl: String,
    private val json: Json
) : WebSocketClient {

    private var session: DefaultClientWebSocketSession? = null

    override fun <T> observeMessages(
        path: String,
        parser: (String) -> T?
    ): Flow<Result<T>> {
        return flow {
            try {
                client.webSocket(
                    host = baseUrl,
                    port = 9443,
                    path = path
                ) {
                    session = this

                    while (isActive) {
                        when(val frame = incoming.receive()) {
                            is Frame.Text -> {
                                val text = frame.readText()
                                parser(text)?.let {
                                    emit(Result.success(it))
                                }
                            }
                            is Frame.Close -> {
                                emit(Result.failure(Exception("WebSocket closed")))
                                break
                            }
                            else -> { println("Unknown frame type: ${frame.frameType}") }
                        }
                    }
                }
            } catch (e: Exception) {
                emit(Result.failure(e))
            }
        }
    }

    override suspend fun sendMessage(path: String, message: String) {
        session?.send(Frame.Text(message))
    }

    override suspend fun closeConnection() {
        session?.close()
        session = null
    }

}