package com.example.thread.thread.utils

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.TimeoutCancellationException

sealed class AppError(open val message: String) {
    data class NetworkError(override val message: String = "No internet connection") : AppError(message)
    data class ServerError(override val message: String = "Internal server error") : AppError(message)
    data class TimeoutError(override val message: String = "Request timeout") : AppError(message)
    data class UnknownError(override val message: String = "Something went wrong") : AppError(message)
    data class ValidationError(override val message: String) : AppError(message)
    data class WebSocketError(override val message: String = "WebSocket connection error") : AppError(message)
}

object ErrorHandler {
    fun handleException(exception: Throwable): AppError {
        return when (exception) {
            is TimeoutCancellationException -> AppError.TimeoutError()
            is ClientRequestException -> {
                when (exception.response.status.value) {
                    400 -> AppError.ValidationError("Bad request")
                    401 -> AppError.ValidationError("Unauthorized")
                    403 -> AppError.ValidationError("Forbidden")
                    404 -> AppError.ValidationError("Not found")
                    else -> AppError.UnknownError(exception.message)
                }
            }
            is ServerResponseException -> {
                AppError.ServerError(exception.message)
            }
            else -> AppError.UnknownError(exception.message ?: "Unknown error")
        }
    }
}
