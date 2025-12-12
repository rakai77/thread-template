package com.example.thread.thread.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotTickResponse(
    @SerialName("Data") val data: SpotTickDataResponse? = null,
    @SerialName("Err") val error: ApiErrorResponse? = null
)

@Serializable
data class SpotTickDataResponse(
    @SerialName("MAPPAIRS") val mappedPairs: List<SpotTickItemResponse>? = null,
    @SerialName("UNMAPPAIRS") val unmappedPairs: List<SpotTickItemResponse>? = null
)

@Serializable
data class SpotTickItemResponse(
    @SerialName("MARKET") val market: String,
    @SerialName("INSTRUMENT") val instrument: String,
    @SerialName("MAPPED_INSTRUMENT") val mappedInstrument: String? = null,
    @SerialName("BASE") val base: String,
    @SerialName("QUOTE") val quote: String,
    @SerialName("LAST_UPDATE_TS") val lastUpdateTs: Long,
    @SerialName("LAST_PROCESSED_TS") val lastProcessedTs: Long? = null,
    @SerialName("LAST_TRADE_QUANTITY") val lastTradeQuantity: Double? = null,
    @SerialName("LAST_TRADE_QUOTE_QUANTITY") val lastTradeQuoteQuantity: Double? = null,
    @SerialName("LAST_TRADE_PRICE") val lastTradePrice: Double? = null,
    @SerialName("LAST_TRADE_SIDE") val lastTradeSide: String? = null,
    @SerialName("LAST_TRADE_ID") val lastTradeId: String? = null,
    @SerialName("LAST_TRADE_CCSEQ") val lastTradeCcseq: Long? = null,
    @SerialName("CURRENT_DAY_OPEN") val currentDayOpen: Double? = null,
    @SerialName("CURRENT_DAY_HIGH") val currentDayHigh: Double? = null,
    @SerialName("CURRENT_DAY_LOW") val currentDayLow: Double? = null,
    @SerialName("CURRENT_DAY_VOLUME") val currentDayVolume: Double? = null,
    @SerialName("CURRENT_DAY_QUOTE_VOLUME") val currentDayQuoteVolume: Double? = null,
    @SerialName("CURRENT_DAY_TOTAL_TRADES") val currentDayTotalTrades: Long? = null
)

@Serializable
data class ApiErrorResponse(
    @SerialName("type") val type: Int? = null,
    @SerialName("message") val message: String? = null,
    @SerialName("other_info") val otherInfo: Map<String, String>? = null
)

// WebSocket Models
@Serializable
data class WebSocketMessage(
    @SerialName("TYPE") val type: String? = null,
    @SerialName("MESSAGE") val message: String? = null,
    @SerialName("INFO") val info: String? = null,
    @SerialName("Data") val data: SpotTickItemResponse? = null,
    @SerialName("Err") val error: ApiErrorResponse? = null
)

@Serializable
data class WebSocketSubscribe(
    @SerialName("action") val action: String = "SubAdd",
    @SerialName("subs") val subscriptions: List<String>
)
