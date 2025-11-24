package com.example.thread.thread.domain.repository

import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.domain.model.CryptoTrade
import com.example.thread.thread.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
     // REST API
     suspend fun get24HourTicker(symbol: String): Resource<CryptoTicker>
     suspend fun getAllTickers(): Resource<List<CryptoTicker>>

     // WebSocket
     fun observeTickerRealTime(symbol: String): Flow<Resource<CryptoTicker>>
     fun observeTradesRealTime(symbol: String): Flow<Resource<CryptoTrade>>
     fun observeMultipleTickersRealTime(symbols: List<String>):Flow<Resource<List<CryptoTicker>>>
     suspend fun stopObserving()
 }