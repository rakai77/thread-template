package com.example.thread.thread.data.model

data class ErrorState(
    val message: String,
    val canRetry: Boolean = true
)
