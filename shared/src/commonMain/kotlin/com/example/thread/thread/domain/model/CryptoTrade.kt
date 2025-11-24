package com.example.thread.thread.domain.model

data class CryptoTrade(
    val symbol: String = "",
    val price: Double = 0.0,
    val quantity: Double = 0.0,
    val tradeTime: Long = 0L
)