package com.example.thread.thread.data.remote.api

import com.example.thread.thread.data.remote.response.SpotTickResponse
import com.example.thread.thread.domain.model.ApiException
import com.example.thread.thread.utils.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class CryptoApiService(
    private val httpClient: HttpClient
) {

    suspend fun getLatestTick(
        market: String? = null,
        instrument: String? = null,
        limit: Int = 10
    ): SpotTickResponse {
        return try {
            httpClient.get(Constants.ENDPOINT_SPOT_LATEST_TICK) {
                parameter("limit", limit)
                market?.let { parameter("market", it) }
                instrument?.let { parameter("instrument", it) }
            }.body()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException.NetworkError(
                "Failed to fetch latest tick: ${e.message}",
                e
            )
        }
    }

    suspend fun getLatestTickByAsset(
        base: String,
        quote: String = "USD",
        limit: Int = 10
    ): SpotTickResponse {
        return try {
            httpClient.get(Constants.ENDPOINT_SPOT_LATEST_TICK_ASSET) {
                parameter("base", base)
                parameter("quote", quote)
                parameter("limit", limit)
            }.body()
        } catch (e: ApiException) {
            throw e
        } catch (e: Exception) {
            throw ApiException.NetworkError(
                "Failed to fetch latest tick by asset: ${e.message}",
                e
            )
        }
    }
}