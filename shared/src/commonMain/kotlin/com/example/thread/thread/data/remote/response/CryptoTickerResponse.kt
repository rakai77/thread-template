package com.example.thread.thread.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class CryptoTickerResponse(
    @SerialName("symbol") val symbol: String,
    @SerialName("price") val price: String,
    @SerialName("timestamp") val timestamp: Long,
    @SerialName("volume_24h") val volume24h: String? = null,
    @SerialName("change_24h") val change24h: String? = null
)

@Serializable
data class WebSocketMessageResponse(
    @SerialName("event") val event: String,
    @SerialName("data") val data: CryptoTickerResponse? = null,
    @SerialName("message") val message: String? = null
)

@Serializable
data class SubscribeRequestResponse(
    @SerialName("event") val event: String = "subscribe",
    @SerialName("data") val data: SubscribeDataResponse

)

@Serializable
data class SubscribeDataResponse(
    @SerialName("channels") val channels: List<String>,
    @SerialName("apiKey") val apiKey: String
)