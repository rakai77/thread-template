package com.example.thread.thread.utils

import kotlinx.coroutines.CoroutineDispatcher

expect object AppDispatchers {
    val io: CoroutineDispatcher
    val main: CoroutineDispatcher
    val default: CoroutineDispatcher
}
