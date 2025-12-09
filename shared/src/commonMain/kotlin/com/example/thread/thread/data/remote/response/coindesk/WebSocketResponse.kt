package com.example.thread.thread.data.remote.response.coindesk

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class CryptoCompareWSMessage(
    @SerialName("TYPE") val type: String? = null,              // "0" for trades, "5" for aggregate
    @SerialName("M") val market: String? = null,               // Exchange name
    @SerialName("FSYM") val fromSymbol: String? = null,        // From currency (e.g., BTC)
    @SerialName("TSYM") val toSymbol: String? = null,          // To currency (e.g., USD)
    @SerialName("P") val price: Double? = null,                // Current price
    @SerialName("TS") val timestamp: Long? = null,             // Trade timestamp
    @SerialName("Q") val quantity: Double? = null,             // Trade quantity
    @SerialName("TOTAL") val total: Double? = null,            // Total trade value
    @SerialName("FLAGS") val flags: String? = null,
    @SerialName("ID") val id: String? = null,

    // For aggregate index type
    @SerialName("PRICE") val currentPrice: Double? = null,
    @SerialName("LASTUPDATE") val lastUpdate: Long? = null,
    @SerialName("LASTVOLUME") val lastVolume: Double? = null,
    @SerialName("LASTVOLUMETO") val lastVolumeTo: Double? = null,
    @SerialName("VOLUMEDAY") val volumeDay: Double? = null,
    @SerialName("VOLUME24HOUR") val volume24Hour: Double? = null,
    @SerialName("OPENDAY") val openDay: Double? = null,
    @SerialName("HIGHDAY") val highDay: Double? = null,
    @SerialName("LOWDAY") val lowDay: Double? = null,
    @SerialName("OPEN24HOUR") val open24Hour: Double? = null,
    @SerialName("HIGH24HOUR") val high24Hour: Double? = null,
    @SerialName("LOW24HOUR") val low24Hour: Double? = null,
    @SerialName("CHANGE24HOUR") val change24Hour: Double? = null,
    @SerialName("CHANGEPCT24HOUR") val changePct24Hour: Double? = null,
    @SerialName("CHANGEDAY") val changeDay: Double? = null,
    @SerialName("CHANGEPCTDAY") val changePctDay: Double? = null,
    @SerialName("SUPPLY") val supply: Double? = null,
    @SerialName("MKTCAP") val marketCap: Double? = null
)
