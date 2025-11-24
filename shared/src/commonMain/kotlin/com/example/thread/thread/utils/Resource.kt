package com.example.thread.thread.utils

sealed class Resource<T>(
    val data: T? = null,
    val error: AppError? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(error: AppError, data: T? = null) : Resource<T>(data, error)
}