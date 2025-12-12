package com.example.thread.thread.di

import com.example.thread.thread.data.remote.api.CryptoApiService
import com.example.thread.thread.getPlatform
import com.example.thread.thread.data.remote.setupHttpClient
import com.example.thread.thread.data.remote.setupWebSocketClient
import com.example.thread.thread.data.remote.websockets.CryptoWebSocketService
import com.example.thread.thread.domain.repository.CryptoRepository
import com.example.thread.thread.domain.repository.CryptoRepositoryImpl
import com.example.thread.thread.domain.usecase.CloseWebSocketUseCase
import com.example.thread.thread.domain.usecase.GetCryptoByAssetUseCase
import com.example.thread.thread.domain.usecase.GetCryptoListUseCase
import com.example.thread.thread.domain.usecase.ObserveCryptoStreamUseCase
import com.example.thread.thread.utils.AppDispatchers
import com.example.thread.thread.utils.Constants
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    // JSON Configuration
    single {
        Json {
            isLenient = true
            ignoreUnknownKeys = true
            prettyPrint = true
            explicitNulls = false
        }
    }

    // HTTP Client for REST API
    single(named("api")) {
        setupHttpClient(
            baseUrl = Constants.BASE_URL_REST,
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }

    // HTTP Client for WebSocket
    single(named("websocket")) {
        setupWebSocketClient(
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }

    // Data Sources
    single { CryptoApiService(get(named("api"))) }
    single { CryptoWebSocketService(get(named("websocket")), get()) }

    // Repository
    single<CryptoRepository> {
        CryptoRepositoryImpl(
            webSocketDataSource = get(),
            apiDataSource = get(),
            dispatchers = AppDispatchers
        )
    }

    // Use Cases
    factory { GetCryptoListUseCase(get()) }
    factory { GetCryptoByAssetUseCase(get()) }
    factory { ObserveCryptoStreamUseCase(get()) }
    factory { CloseWebSocketUseCase(get()) }
}