package com.example.thread.thread.domain.repository

import com.example.thread.thread.data.remote.response.coindesk.CoinMarketCapData
import com.example.thread.thread.data.remote.response.coindesk.TopMarketCapResponse
import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.domain.model.CryptoTrade
import com.example.thread.thread.domain.model.coindesk.CryptoStreamUpdate
import com.example.thread.thread.domain.model.coindesk.TopMarketCapResult
import com.example.thread.thread.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
    suspend fun getTopMarketCap(
        currency: String,
        limit: Int,
        page: Int
    ): Result<TopMarketCapResult>

    fun watchTopMarketCap(
        currency: String,
        limit: Int
    ): Flow<Result<List<CryptoStreamUpdate>>>

    fun watchCoinPrice(
        fromSymbol: String,
        toSymbol: String
    ): Flow<Result<CryptoStreamUpdate>>
     suspend fun stopObserving()
 }