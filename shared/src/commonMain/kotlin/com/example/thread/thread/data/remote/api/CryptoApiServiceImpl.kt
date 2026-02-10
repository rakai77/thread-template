package com.example.thread.thread.data.remote.api

import com.example.thread.thread.data.remote.response.coindesk.TopMarketCapResponse
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
    ): TopMarketCapResponse {
        return httpClient.get(Endpoint.TOP_MARKET_CAP_ENDPOINT) {
            parameter("tsym", currency)
            parameter("limit", limit)
            parameter("page", page)
        }.body()
    }

}