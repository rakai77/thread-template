package com.example.thread.thread.domain.repository

import com.example.thread.thread.domain.model.coindesk.CryptoStreamUpdate
import com.example.thread.thread.domain.model.coindesk.TopMarketCapResult
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
    suspend fun getTopMarketCap(
        currency: String,
        limit: Int,
        page: Int
    ): Flow<TopMarketCapResult>

    fun watchTopMarketCap(
        currency: String,
        limit: Int
    ): Flow<Result<List<CryptoStreamUpdate>>>

     suspend fun stopObserving()
 }