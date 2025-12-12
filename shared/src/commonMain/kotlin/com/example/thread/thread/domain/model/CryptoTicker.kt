package com.example.thread.thread.domain.model

data class CryptoTicker(
    // Basic info
    val market: String,
    val instrument: String,
    val base: String,
    val quote: String,

    // Price data
    val lastTradePrice: Double,
    val lastUpdateTimestamp: Long,
    val lastTradeQuantity: Double?,

    // 24h data
    val volume24h: Double?,
    val currentDayOpen: Double?,
    val currentDayHigh: Double?,
    val currentDayLow: Double?,
    val priceChange24h: Double?,
    val priceChangePercentage24h: Double?,
    val totalTrades24h: Long?,

    // Formatted strings (KMP-compatible)
    val formattedPrice: String,
    val formattedChange: String?,
    val formattedVolume: String?,
    val formattedLastUpdate: String
)

