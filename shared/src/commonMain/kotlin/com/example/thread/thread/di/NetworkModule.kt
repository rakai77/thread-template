package com.example.thread.thread.di

import com.example.thread.thread.data.remote.api.CryptoApiService
import com.example.thread.thread.data.remote.api.CryptoApiServiceImpl
import com.example.thread.thread.data.remote.createJsonParser
import com.example.thread.thread.data.remote.setupHttpClient
import com.example.thread.thread.data.remote.websocket.WebSocketClient
import com.example.thread.thread.data.remote.websocket.WebSocketClientImpl
import com.example.thread.thread.data.remote.websocket.WebSocketService
import com.example.thread.thread.data.remote.websocket.WebSocketServiceImpl
import com.example.thread.thread.domain.repository.CryptoRepository
import com.example.thread.thread.domain.repository.CryptoRepositoryImpl
import com.example.thread.thread.domain.usecase.GetTopMarketUseCase
import com.example.thread.thread.domain.usecase.WatchCoinPriceUseCase
import com.example.thread.thread.domain.usecase.WatchTopMarketCapUseCase
import com.example.thread.thread.getPlatform
import com.example.thread.thread.utils.Constant
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val remoteModule = module {

    single { createJsonParser() }

    single {
        setupHttpClient(
            baseUrl = Constant.BASE_URL,
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }

    single<WebSocketClient> {
        WebSocketClientImpl(
            client = get(),
            json = get()
        )
    }

    single<CryptoApiService> { CryptoApiServiceImpl(httpClient = get(), json = get()) }

    single<WebSocketService> {
        WebSocketServiceImpl(
            webSocketClient = get(),
            json = get()
        )
    }

    single<CryptoRepository> { CryptoRepositoryImpl(apiService = get(), webSocketService = get()) }

    singleOf(::GetTopMarketUseCase)
    singleOf(::WatchTopMarketCapUseCase)
    singleOf(::WatchCoinPriceUseCase)
}