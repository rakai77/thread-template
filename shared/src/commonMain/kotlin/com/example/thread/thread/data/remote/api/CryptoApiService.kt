package com.example.thread.thread.data.remote.api

import com.example.thread.thread.data.remote.response.CryptoTickerResponse

interface CryptoApiService {
    suspend fun get24HourTicker(symbol: String): Result<CryptoTickerResponse>
    suspend fun getAllTickers(): Result<List<CryptoTickerResponse>>
    suspend fun getServerTime(): Result<Long>
}