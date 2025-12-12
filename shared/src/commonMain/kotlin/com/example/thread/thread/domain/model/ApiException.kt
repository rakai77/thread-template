package com.example.thread.thread.domain.model

import com.example.thread.thread.data.remote.response.ApiErrorResponse

sealed class ApiException(
    override val message: String,
    override val cause: Throwable? = null
) : Exception(message, cause) {

    // HTTP Errors
    data class NetworkError(
        override val message: String = "Network connection failed",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    data class TimeoutError(
        override val message: String = "Request timeout",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    data class ServerError(
        val code: Int,
        override val message: String = "Server error",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    // API Specific Errors
    data class AuthenticationError(
        override val message: String = "Authentication failed",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    data class RateLimitError(
        override val message: String = "Rate limit exceeded",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    data class InvalidParametersError(
        override val message: String = "Invalid request parameters",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    data class DataNotFoundError(
        override val message: String = "Requested data not found",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    // WebSocket Errors
    data class WebSocketConnectionError(
        override val message: String = "WebSocket connection failed",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    data class WebSocketAuthError(
        override val message: String = "WebSocket authentication failed",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    // Generic
    data class UnknownError(
        override val message: String = "Unknown error occurred",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)

    data class ParsingError(
        override val message: String = "Failed to parse response",
        override val cause: Throwable? = null
    ) : ApiException(message, cause)
}

// Error Mapper
fun ApiErrorResponse.toException(): ApiException {
    return when (type) {
        1 -> ApiException.InvalidParametersError(message ?: "Invalid parameters")
        2 -> ApiException.AuthenticationError(message ?: "Authentication failed")
        3 -> ApiException.RateLimitError(message ?: "Rate limit exceeded")
        4 -> ApiException.DataNotFoundError(message ?: "Data not found")
        5 -> ApiException.ServerError(500, message ?: "Internal server error")
        else -> ApiException.UnknownError(message ?: "Unknown error")
    }
}
