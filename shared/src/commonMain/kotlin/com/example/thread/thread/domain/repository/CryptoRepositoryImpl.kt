package com.example.thread.thread.domain.repository

import com.example.thread.thread.data.Result
import com.example.thread.thread.data.remote.api.CryptoApiService
import com.example.thread.thread.data.remote.websockets.CryptoWebSocketService
import com.example.thread.thread.domain.model.ApiException
import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.domain.model.toException
import com.example.thread.thread.utils.AppDispatchers
import com.example.thread.thread.utils.toDomain
import com.example.thread.thread.utils.toDomainList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class CryptoRepositoryImpl(
    private val webSocketDataSource: CryptoWebSocketService,
    private val apiDataSource: CryptoApiService,
    private val dispatchers: AppDispatchers
) : CryptoRepository {

    override suspend fun getCryptoList(
        market: String?,
        instrument: String?
    ): Result<List<CryptoTicker>> = withContext(dispatchers.io) {
        try {
            val response = apiDataSource.getLatestTick(
                market = market,
                instrument = instrument,
                limit = 10
            )

            // Check for API error in response
            if (response.error != null) {
                val exception = response.error.toException()
                return@withContext Result.Error(exception, exception.message)
            }

            // Map DTO to domain model
            val domainList = toDomainList(response)

            if (domainList.isEmpty()) {
                Result.Error(
                    ApiException.DataNotFoundError("No crypto data available"),
                    "No crypto data available"
                )
            } else {
                Result.Success(domainList)
            }
        } catch (e: ApiException) {
            Result.Error(e, e.message)
        } catch (e: Exception) {
            val exception = ApiException.UnknownError(
                e.message ?: "Failed to fetch crypto list",
                e
            )
            Result.Error(exception, exception.message)
        }
    }

    override suspend fun getCryptoByAsset(
        base: String,
        quote: String
    ): Result<List<CryptoTicker>> = withContext(dispatchers.io) {
        try {
            // Validate inputs
            if (base.isBlank()) {
                return@withContext Result.Error(
                    ApiException.InvalidParametersError("Base asset cannot be empty"),
                    "Base asset cannot be empty"
                )
            }

            val response = apiDataSource.getLatestTickByAsset(
                base = base.uppercase(),
                quote = quote.uppercase(),
                limit = 10
            )

            // Check for API error in response
            if (response.error != null) {
                val exception = response.error.toException()
                return@withContext Result.Error(exception, exception.message)
            }

            // Map DTO to domain model
            val domainList = toDomainList(response)

            if (domainList.isEmpty()) {
                Result.Error(
                    ApiException.DataNotFoundError(
                        "No data found for $base/$quote"
                    ),
                    "No data found for $base/$quote"
                )
            } else {
                Result.Success(domainList)
            }
        } catch (e: ApiException) {
            Result.Error(e, e.message)
        } catch (e: Exception) {
            val exception = ApiException.UnknownError(
                e.message ?: "Failed to fetch crypto by asset",
                e
            )
            Result.Error(exception, exception.message)
        }
    }

    override fun observeCryptoStream(
        subscriptions: List<String>
    ): Flow<Result<CryptoTicker>> {
        return webSocketDataSource.observeSpotTicks(subscriptions)
            .map { result ->
                when (result) {
                    is Result.Success -> {
                        try {
                            Result.Success(toDomain(result.data))
                        } catch (e: Exception) {
                            Result.Error(
                                ApiException.ParsingError(
                                    "Failed to map WebSocket data",
                                    e
                                ),
                                "Failed to map WebSocket data"
                            )
                        }
                    }
                    is Result.Error -> result
                }
            }
    }

    override suspend fun unsubscribeWebSocket(subscriptions: List<String>) {
        try {
            webSocketDataSource.unsubscribe(subscriptions)
        } catch (e: Exception) {
            // Log error but don't throw
            println("Failed to unsubscribe: ${e.message}")
        }
    }

    override suspend fun closeWebSocket() {
        try {
            webSocketDataSource.closeConnection()
        } catch (e: Exception) {
            // Log error but don't throw
            println("Failed to close WebSocket: ${e.message}")
        }
    }
}