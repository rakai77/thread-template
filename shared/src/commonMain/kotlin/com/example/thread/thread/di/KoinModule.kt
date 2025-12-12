package com.example.thread.thread.di

import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

fun initKoinModule(
    additionalModules: List<Module> = emptyList(),
    appDeclaration: KoinAppDeclaration = {}
) {
    runCatching { stopKoin() }
    startKoin {
        appDeclaration()
        modules(listOf(networkModule) + additionalModules)
    }
}