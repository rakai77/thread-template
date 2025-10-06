package com.example.thread.thread

import androidx.compose.ui.window.ComposeUIViewController
import com.example.thread.thread.di.initKoinModule
import com.example.thread.thread.presentation.screen.ThreadApp

fun MainViewController() = ComposeUIViewController(
    configure = { initKoinModule {  } }
) {
    ThreadApp()
}