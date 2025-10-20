package com.example.thread.thread.di

import com.example.thread.thread.getPlatform
import com.example.thread.thread.utils.setupHttpClient
import org.koin.dsl.module

val remoteModule = module {
    single {
        setupHttpClient(
            baseUrl = "https://api.themoviedb.org/3",
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }
}