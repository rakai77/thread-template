package com.example.thread.thread.domain.repository

import com.example.thread.thread.data.remote.api.CryptoApiService
import com.example.thread.thread.data.remote.websocket.BinanceWebSocketService
import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.domain.model.CryptoTrade
import com.example.thread.thread.utils.ErrorHandler
import com.example.thread.thread.utils.Resource
import com.example.thread.thread.utils.toDomain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CryptoRepositoryImpl(
    private val apiService: CryptoApiService,
    private val webSocketService: BinanceWebSocketService
) : CryptoRepository {

    override suspend fun get24HourTicker(symbol: String): Resource<CryptoTicker> {
        return apiService.get24HourTicker(symbol).fold(
            onSuccess = { response ->
                Resource.Success(response.toDomain())
            },
            onFailure = {
                Resource.Error(ErrorHandler.handleException(it))
            }
        )
    }

    override suspend fun getAllTickers(): Resource<List<CryptoTicker>> {
        return apiService.getAllTickers().fold(
            onSuccess = { dtoList ->
                Resource.Success(dtoList.map { it.toDomain() })
            },
            onFailure = { exception ->
                Resource.Error(ErrorHandler.handleException(exception))
            }
        )
    }

    override fun observeTickerRealTime(symbol: String): Flow<Resource<CryptoTicker>> {
        return webSocketService.subscribeToTicker(symbol).map { resource ->
            resource.fold(
                onSuccess = { dto ->
                    Resource.Success(dto.toDomain())
                },
                onFailure = { exception ->
                    Resource.Error(ErrorHandler.handleException(exception))
                }
            )
        }
    }

    override fun observeTradesRealTime(symbol: String): Flow<Resource<CryptoTrade>> {
        return webSocketService.subscribeToTrades(symbol).map { result ->
            result.fold(
                onSuccess = { dto ->
                    Resource.Success(dto.toDomain())
                },
                onFailure = { exception ->
                    Resource.Error(ErrorHandler.handleException(exception))
                }
            )
        }
    }

    override fun observeMultipleTickersRealTime(symbols: List<String>): Flow<Resource<List<CryptoTicker>>> {
        return webSocketService.subscribeToMultipleTickers(symbols).map { result ->
            result.fold(
                onSuccess = { dtoList ->
                    Resource.Success(dtoList.map { it.toDomain() })
                },
                onFailure = { exception ->
                    Resource.Error(ErrorHandler.handleException(exception))
                }
            )
        }
    }

    override suspend fun stopObserving() {
        webSocketService.unsubscribe()
    }
}