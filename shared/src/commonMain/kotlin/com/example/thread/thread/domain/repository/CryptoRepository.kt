package com.example.thread.thread.domain.repository

import com.example.thread.thread.data.Result
import com.example.thread.thread.domain.model.CryptoTicker
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {

    /**
     * Fetch latest crypto tickers from REST API
     * @param market Optional market filter (e.g., "coinbase", "kraken")
     * @param instrument Optional instrument filter (e.g., "BTC-USD")
     * @return Result with list of CryptoTicker (max 10 items)
     */
    suspend fun getCryptoList(
        market: String? = null,
        instrument: String? = null
    ): Result<List<CryptoTicker>>

    /**
     * Fetch crypto tickers by base and quote asset
     * @param base Base asset (e.g., "BTC", "ETH")
     * @param quote Quote asset (e.g., "USD", "EUR")
     * @return Result with list of CryptoTicker (max 10 items)
     */
    suspend fun getCryptoByAsset(
        base: String,
        quote: String = "USD"
    ): Result<List<CryptoTicker>>

    /**
     * Observe real-time crypto tickers via WebSocket
     * @param subscriptions List of subscription channels
     * Example: ["2~Coinbase~BTC~USD", "2~Kraken~ETH~USD"]
     * @return Flow of Result<CryptoTicker>
     */
    fun observeCryptoStream(
        subscriptions: List<String>
    ): Flow<Result<CryptoTicker>>

    /**
     * Unsubscribe from specific WebSocket channels
     */
    suspend fun unsubscribeWebSocket(subscriptions: List<String>)

    /**
     * Close WebSocket connection
     */
    suspend fun closeWebSocket()
}