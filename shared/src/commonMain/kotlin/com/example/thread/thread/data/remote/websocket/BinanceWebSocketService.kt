package com.example.thread.thread.data.remote.websocket

import com.example.thread.thread.data.remote.response.CryptoTickerResponse
import com.example.thread.thread.data.remote.response.CryptoTradeResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

interface BinanceWebSocketService {
    fun subscribeToTicker(symbol: String): Flow<Result<CryptoTickerResponse>>
    fun subscribeToTrades(symbol: String): Flow<Result<CryptoTradeResponse>>
    fun subscribeToMultipleTickers(symbols: List<String>): Flow<Result<List<CryptoTickerResponse>>>
    suspend fun unsubscribe()
}

class BinanceWebSocketServiceImpl(
    private val webSocketClient: WebSocketClient,
    private val json: Json
) : BinanceWebSocketService {

    override fun subscribeToTicker(symbol: String): Flow<Result<CryptoTickerResponse>> {
        val path = "/ws/${symbol.lowercase()}@ticker"
        return webSocketClient.observeMessages(path) { message ->
            try {
                json.decodeFromString<CryptoTickerResponse>(message)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun subscribeToTrades(symbol: String): Flow<Result<CryptoTradeResponse>> {
        val path = "/ws/${symbol.lowercase()}@trade"
        return webSocketClient.observeMessages(path) { message ->
            try {
                json.decodeFromString<CryptoTradeResponse>(message)
            } catch (e: Exception) {
                null
            }
        }
    }

    override fun subscribeToMultipleTickers(symbols: List<String>): Flow<Result<List<CryptoTickerResponse>>> {
        val stream = symbols.joinToString("/") { "${it.lowercase()}@ticker" }
        val path = "/stream?streams=$stream"
        return webSocketClient.observeMessages(path) { message ->
            try {
                // Parse multiple ticker messages
                val tickers = symbols.mapNotNull { _ ->
                    try {
                        json.decodeFromString<CryptoTickerResponse>(message)
                    } catch (e: Exception) {
                        null
                    }
                }
                tickers.ifEmpty { null }
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun unsubscribe() {
        webSocketClient.closeConnection()
    }

}