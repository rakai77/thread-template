package com.example.thread.thread.utils

import com.example.thread.thread.data.remote.response.CryptoTickerResponse
import com.example.thread.thread.data.remote.response.CryptoTradeResponse
import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.domain.model.CryptoTrade

// Mapper functions
fun CryptoTickerResponse.toDomain() = CryptoTicker(
    symbol = symbol,
    currentPrice = currentPrice.toDoubleOrNull() ?: 0.0,
    priceChange = priceChange.toDoubleOrNull() ?: 0.0,
    priceChangePercent = priceChangePercent.toDoubleOrNull() ?: 0.0,
    highPrice = highPrice.toDoubleOrNull() ?: 0.0,
    lowPrice = lowPrice.toDoubleOrNull() ?: 0.0,
    volume = volume.toDoubleOrNull() ?: 0.0,
    quoteVolume = quoteVolume.toDoubleOrNull() ?: 0.0,
    eventTime = eventTime
)

fun CryptoTradeResponse.toDomain() = CryptoTrade(
    symbol = symbol,
    price = price.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toDoubleOrNull() ?: 0.0,
    tradeTime = tradeTime
)