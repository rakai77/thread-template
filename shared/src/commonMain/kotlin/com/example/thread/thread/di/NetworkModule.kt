package com.example.thread.thread.di

import com.example.thread.thread.getPlatform
import com.example.thread.thread.utils.createJsonParser
import com.example.thread.thread.utils.setupHttpClient
import org.koin.dsl.module

val remoteModule = module {

    single {
        createJsonParser()
    }

    single {
        setupHttpClient(
            baseUrl = "https://api.binance.com",
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }
}