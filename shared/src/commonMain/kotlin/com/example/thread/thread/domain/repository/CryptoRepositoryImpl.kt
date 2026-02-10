package com.example.thread.thread.domain.repository

import com.example.thread.thread.data.remote.api.CryptoApiService
import com.example.thread.thread.data.remote.response.coindesk.CryptoCompareWSMessage
import com.example.thread.thread.data.remote.websocket.WebSocketService
import com.example.thread.thread.domain.model.coindesk.CryptoStreamUpdate
import com.example.thread.thread.domain.model.coindesk.TopMarketCapResult
import com.example.thread.thread.utils.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

class CryptoRepositoryImpl(
    private val apiService: CryptoApiService,
    private val webSocketService: WebSocketService
) : CryptoRepository {

    override suspend fun getTopMarketCap(
        currency: String,
        limit: Int,
        page: Int
    ): Flow<TopMarketCapResult> {
        return flow {
            val apiService = apiService.getTopMarketCap(currency, limit, page)
            emit(apiService.toDomain())
        }
    }

    override fun watchTopMarketCap(
        currency: String,
        limit: Int
    ): Flow<Result<List<CryptoStreamUpdate>>> {
        return flow {
            val updates = mutableMapOf<String, CryptoStreamUpdate>()
            webSocketService.subscribeToMultipleCoins(
                coins = listOf("BTC", "ETH", "BNB", "XRP", "ADA", "SOL", "DOT", "DOGE", "AVAX", "MATIC"),
                currency = currency
            ).collect { result ->
                result.onSuccess { wsMessage ->
                    val update = mapWSMessageToStreamUpdate(wsMessage)
                    if (update != null) {
                        updates[update.symbol] = update
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

    override suspend fun stopObserving() {
        webSocketService.unsubscribe()
    }

    private fun mapWSMessageToStreamUpdate(
        wsMessage: CryptoCompareWSMessage,
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