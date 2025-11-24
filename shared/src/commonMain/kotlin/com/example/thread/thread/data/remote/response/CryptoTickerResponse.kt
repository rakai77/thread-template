package com.example.thread.thread.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoTickerResponse(
    @SerialName("e") val eventType: String,
    @SerialName("E") val eventTime: Long,
    @SerialName("s") val symbol: String,
    @SerialName("c") val currentPrice: String,
    @SerialName("p") val priceChange: String,
    @SerialName("P") val priceChangePercent: String,
    @SerialName("h") val highPrice: String,
    @SerialName("l") val lowPrice: String,
    @SerialName("v") val volume: String,
    @SerialName("q") val quoteVolume: String
)