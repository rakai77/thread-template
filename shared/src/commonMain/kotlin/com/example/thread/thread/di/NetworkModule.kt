package com.example.thread.thread.di

import com.example.thread.thread.data.remote.api.CryptoApiService
import com.example.thread.thread.data.remote.api.CryptoApiServiceImpl
import com.example.thread.thread.getPlatform
import com.example.thread.thread.data.remote.createJsonParser
import com.example.thread.thread.data.remote.setupHttpClient
import com.example.thread.thread.data.remote.websocket.BinanceWebSocketService
import com.example.thread.thread.data.remote.websocket.BinanceWebSocketServiceImpl
import com.example.thread.thread.data.remote.websocket.WebSocketClient
import com.example.thread.thread.data.remote.websocket.WebSocketClientImpl
import com.example.thread.thread.domain.repository.CryptoRepository
import com.example.thread.thread.domain.repository.CryptoRepositoryImpl
import com.example.thread.thread.domain.usecase.GetAllCryptoTickerUseCase
import com.example.thread.thread.domain.usecase.GetCryptoTickerUseCase
import com.example.thread.thread.domain.usecase.ObserveCryptoTickerRealTimeUseCase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val remoteModule = module {

    single { createJsonParser() }

    single {
        setupHttpClient(
            baseUrl = "api.binance.com",
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }

    single<WebSocketClient> {
        WebSocketClientImpl(
            client = get(),
            baseUrl = "stream.binance.com",
            json = get()
        )
    }

    single<CryptoApiService> { CryptoApiServiceImpl(httpClient = get(), json = get()) }

    single<BinanceWebSocketService> {
        BinanceWebSocketServiceImpl(
            webSocketClient = get(),
            json = get()
        )
    }

    single<CryptoRepository> { CryptoRepositoryImpl(apiService = get(), webSocketService = get()) }

    singleOf(::GetCryptoTickerUseCase)
    singleOf(::GetAllCryptoTickerUseCase)
    singleOf(::ObserveCryptoTickerRealTimeUseCase)
}