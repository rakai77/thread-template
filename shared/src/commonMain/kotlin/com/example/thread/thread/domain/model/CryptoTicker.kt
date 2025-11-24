package com.example.thread.thread.domain.model

data class CryptoTicker(
    val symbol: String = "",
    val currentPrice: Double = 0.0,
    val priceChange: Double = 0.0,
    val priceChangePercent: Double = 0.0,
    val highPrice: Double = 0.0,
    val lowPrice: Double = 0.0,
    val volume: Double = 0.0,
    val quoteVolume: Double = 0.0,
    val eventTime: Long = 0L
)