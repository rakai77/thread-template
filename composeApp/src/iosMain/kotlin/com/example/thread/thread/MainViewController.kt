package com.example.thread.thread

import androidx.compose.ui.window.ComposeUIViewController
import com.example.thread.thread.di.initKoinModule

fun MainViewController() = ComposeUIViewController(
    configure = { initKoinModule {  } }
) {
    App()
}