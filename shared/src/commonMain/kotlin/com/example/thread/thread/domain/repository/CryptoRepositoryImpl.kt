package com.example.thread.thread.domain.repository

import com.example.thread.thread.data.remote.api.CryptoApiService
import com.example.thread.thread.data.remote.response.coindesk.CryptoCompareWSMessage
import com.example.thread.thread.data.remote.websocket.WebSocketService
import com.example.thread.thread.domain.model.coindesk.CryptoStreamUpdate
import com.example.thread.thread.domain.model.coindesk.TopMarketCapResult
import com.example.thread.thread.utils.mapTopMarketCapResponseToDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class CryptoRepositoryImpl(
    private val apiService: CryptoApiService,
    private val webSocketService: WebSocketService
) : CryptoRepository {

    override suspend fun getTopMarketCap(
        currency: String,
        limit: Int,
        page: Int
    ): Result<TopMarketCapResult> {
        return try {
            apiService.getTopMarketCap(currency, limit, page)
                .map { response ->
                    mapTopMarketCapResponseToDomain(response, currency)
                }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ✅ FIX: Watch multiple coins with correct mapping
    override fun watchTopMarketCap(
        currency: String,
        limit: Int
    ): Flow<Result<List<CryptoStreamUpdate>>> {
        // This flow will collect individual coin updates
        return flow {
            // We'll buffer updates and emit them periodically
            val updates = mutableMapOf<String, CryptoStreamUpdate>()

            webSocketService.subscribeToMultipleCoins(
                coins = listOf("BTC", "ETH", "BNB", "XRP", "ADA", "SOL", "DOT", "DOGE", "AVAX", "MATIC"),
                currency = currency
            ).collect { result ->
                result.onSuccess { wsMessage ->
                    val update = mapWSMessageToStreamUpdate(wsMessage, currency)
                    if (update != null) {
                        updates[update.symbol] = update
                        // Emit current state of all updates
                        emit(Result.success(updates.values.toList().take(limit)))
                    }
                }.onFailure { error ->
                    emit(Result.failure(error))
                }
            }
        }.catch { error ->
            emit(Result.failure(Exception("WebSocket stream error: ${error.message}")))
        }
    }

    override fun watchCoinPrice(
        fromSymbol: String,
        toSymbol: String
    ): Flow<Result<CryptoStreamUpdate>> {
        return webSocketService.subscribeToCoinPrice(fromSymbol, toSymbol)
            .map { result ->
                result.mapCatching { wsMessage ->
                    mapWSMessageToStreamUpdate(wsMessage, toSymbol)
                        ?: throw IllegalStateException("Failed to map coin data")
                }
            }
            .catch { error ->
                emit(Result.failure(Exception("Coin price stream error: ${error.message}")))
            }
    }

    override suspend fun stopObserving() {
        webSocketService.unsubscribe()
    }

    // ✅ NEW: Map WebSocket message to stream update
    private fun mapWSMessageToStreamUpdate(
        wsMessage: CryptoCompareWSMessage,
        currency: String
    ): CryptoStreamUpdate? {
        val symbol = wsMessage.fromSymbol ?: return null
        val price = wsMessage.currentPrice ?: wsMessage.price ?: return null

        return CryptoStreamUpdate(
            coinId = symbol,
            symbol = symbol,
            price = price,
            change = wsMessage.change24Hour ?: wsMessage.changeDay ?: 0.0,
            changePct = wsMessage.changePct24Hour ?: wsMessage.changePctDay ?: 0.0,
            volume = wsMessage.volume24Hour ?: wsMessage.volumeDay ?: 0.0,
            timestamp = wsMessage.lastUpdate ?: wsMessage.timestamp ?: 0
        )
    }
}