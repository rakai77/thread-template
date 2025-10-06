package com.example.thread.thread.di

import com.example.thread.thread.getPlatform
import com.example.thread.thread.utils.setupHttpClient
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

fun initKoinModule(appDeclaration: KoinAppDeclaration = {}) {
    runCatching { stopKoin() }
    startKoin {
        appDeclaration()
    }
}

val remoteModule = module {
    single {
        setupHttpClient(
            baseUrl = "https://api.themoviedb.org/3",
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }
}