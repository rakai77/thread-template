package com.example.thread.thread.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoinModule(appDeclaration: KoinAppDeclaration = {}) {
    runCatching { stopKoin() }
    startKoin {
        appDeclaration()
        modules(listOf(remoteModule))
    }
}