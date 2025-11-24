package com.example.thread.thread.data.remote.api

import com.example.thread.thread.data.remote.response.CryptoTickerResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class CryptoApiServiceImpl(
    private val httpClient: HttpClient
) : CryptoApiService {

    override suspend fun get24HourTicker(symbol: String): Result<CryptoTickerResponse> {
        return try {
            val response = httpClient.get(Endpoint.GET_24_HOUR_TICKER) {
                parameter("symbol", symbol)
            }.body<CryptoTickerResponse>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllTickers(): Result<List<CryptoTickerResponse>> {
        return try {
            val response = httpClient.get(Endpoint.GET_24_HOUR_TICKER)
                .body<List<CryptoTickerResponse>>()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getServerTime(): Result<Long> {
        return try {
            val response = httpClient.get(Endpoint.GET_SERVER_TIME)
                .body<Map<String, Long>>()
            Result.success(response["serverTime"] ?: 0L)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}