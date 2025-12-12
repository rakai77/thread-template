package com.example.thread.thread.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual object AppDispatchers {
    actual val io: CoroutineDispatcher = Dispatchers.IO
    actual val main: CoroutineDispatcher = Dispatchers.Main
    actual val default: CoroutineDispatcher = Dispatchers.Default
}