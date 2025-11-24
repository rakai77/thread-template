package com.example.thread.thread.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CryptoTradeResponse(
    @SerialName("e") val eventType: String,
    @SerialName("E") val eventTime: Long,
    @SerialName("s") val symbol: String,
    @SerialName("p") val price: String,
    @SerialName("q") val quantity: String,
    @SerialName("T") val tradeTime: Long
)
