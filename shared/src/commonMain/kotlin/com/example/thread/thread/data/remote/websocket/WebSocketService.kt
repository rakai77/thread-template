package com.example.thread.thread.data.remote.websocket

import com.example.thread.thread.data.remote.response.coindesk.CryptoCompareWSMessage
import com.example.thread.thread.utils.Constant
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.retry
import kotlinx.serialization.json.*

interface WebSocketService {
    fun subscribeToMultipleCoins(
        coins: List<String>,
        currency: String = "USD"
    ): Flow<Result<CryptoCompareWSMessage>>

    suspend fun unsubscribe()
}

class WebSocketServiceImpl(
    private val webSocketClient: WebSocketClient,
    private val json: Json
) : WebSocketService {

    // ✅ FIX: Subscribe to multiple coins using v2 format
    override fun subscribeToMultipleCoins(
        coins: List<String>,
        currency: String
    ): Flow<Result<CryptoCompareWSMessage>> {
        return webSocketClient.observeMessages(
            host = Constant.WS_HOST,
            path = Constant.WS_PATH,
            parser = { message ->
                try {
                    println("🔍 Parsing: $message")

                    // CryptoCompare v2 sends JSON messages
                    val wsMessage = json.decodeFromString<CryptoCompareWSMessage>(message)

                    // Only process trade (0) or aggregate (5) messages with valid price
                    if ((wsMessage.type == "0" || wsMessage.type == "5") &&
                        (wsMessage.price != null || wsMessage.currentPrice != null)) {
                        wsMessage
                    } else {
                        null
                    }
                } catch (e: Exception) {
                    println("❌ Parse error: ${e.message}")
                    null
                }
            },
            onConnect = {
                // ✅ Subscribe to multiple coins using CCCAGG (aggregate index)
                val subs = coins.map { coin ->
                    "5~CCCAGG~$coin~$currency"  // Format: TYPE~EXCHANGE~FROM~TO
                }

                val subscriptionMessage = buildJsonObject {
                    put("action", "SubAdd")
                    putJsonArray("subs") {
                        subs.forEach { sub ->
                            add(sub)
                        }
                    }
                }

                val messageText = json.encodeToString(subscriptionMessage)
                println("📤 Subscribing to: $messageText")
                send(Frame.Text(messageText))
            }
        ).retry(3) { cause ->
            println("🔄 Retrying connection... Cause: ${cause.message}")
            delay(2000)
            true
        }
    }

    override suspend fun unsubscribe() {
        webSocketClient.closeConnection()
    }
}