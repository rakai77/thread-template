package com.example.thread.thread.domain.repository

import com.example.thread.thread.data.Result
import com.example.thread.thread.domain.model.CryptoTicker
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {

    suspend fun getCryptoList(
        market: String? = null,
        instrument: String? = null
    ): Result<List<CryptoTicker>>
    suspend fun getCryptoByAsset(
        base: String,
        quote: String = "USD"
    ): Result<List<CryptoTicker>>

    fun observeCryptoStream(
        subscriptions: List<String>
    ): Flow<Result<CryptoTicker>>

    suspend fun unsubscribeWebSocket(subscriptions: List<String>)

    suspend fun closeWebSocket()
}