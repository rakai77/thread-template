package com.example.thread.thread.domain.model.coindesk

data class TopMarketCapResult(
    val coins: List<CoinMarketCap>,
    val metaData: MarketCapMetaData?,
    val hasWarning: Boolean
)

data class CoinMarketCap(
    val id: String,
    val name: String,
    val fullName: String,
    val symbol: String,
    val imageUrl: String?,
    val algorithm: String?,
    val proofType: String?,
    val rating: CoinRating?,
    val marketData: MarketData?,
    val displayData: DisplayData?
)

data class CoinRating(
    val weissRating: String?,
    val technologyRating: String?,
    val marketPerformanceRating: String?
)

data class MarketData(
    val price: Double,
    val marketCap: Double,
    val circulatingSupply: Double?,
    val totalSupply: Double?,
    val volume24h: Double,
    val volumeDay: Double,
    val change24h: Double,
    val changePct24h: Double,
    val changeDay: Double,
    val changePctDay: Double,
    val high24h: Double,
    val low24h: Double,
    val open24h: Double,
    val highDay: Double,
    val lowDay: Double,
    val openDay: Double,
    val lastUpdate: Long,
    val fromSymbol: String,
    val toSymbol: String
)

data class DisplayData(
    val price: String,
    val marketCap: String,
    val volume24h: String,
    val change24h: String,
    val changePct24h: String,
    val high24h: String,
    val low24h: String
)

data class MarketCapMetaData(
    val totalCount: Int
)

// WebSocket streaming model
data class CryptoStreamUpdate(
    val coinId: String,
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePct: Double,
    val volume: Double,
    val timestamp: Long
)
