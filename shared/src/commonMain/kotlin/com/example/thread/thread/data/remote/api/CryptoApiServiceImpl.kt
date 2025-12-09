package com.example.thread.thread.data.remote.api

import com.example.thread.thread.data.remote.response.CryptoTickerResponse
import com.example.thread.thread.data.remote.response.coindesk.TopMarketCapResponse
import com.example.thread.thread.utils.Constant
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.Json

class CryptoApiServiceImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : CryptoApiService {

    override suspend fun getTopMarketCap(
        currency: String,
        limit: Int,
        page: Int
    ): Result<TopMarketCapResponse> {
        return try {
            val response = httpClient.get(Endpoint.TOP_MARKET_CAP_ENDPOINT) {
                parameter("tsym", currency)
                parameter("limit", limit)
                parameter("page", page)
            }
            Result.success(response.body<TopMarketCapResponse>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}