package com.example.thread.thread.utils

import com.example.thread.thread.data.remote.response.coindesk.*
import com.example.thread.thread.domain.model.coindesk.*
import com.example.thread.thread.domain.model.coindesk.MarketCapMetaData

fun mapTopMarketCapResponseToDomain(
    response: TopMarketCapResponse,
    currency: String
): TopMarketCapResult {
    return TopMarketCapResult(
        coins = response.data?.mapNotNull {
            mapCoinDataToDomain(it, currency)
        } ?: emptyList(),
        metaData = response.metaData?.let {
            MarketCapMetaData(totalCount = it.count ?: 0)
        },
        hasWarning = response.hasWarning ?: false
    )
}

fun mapCoinDataToDomain(
    data: CoinMarketCapData,
    currency: String
): CoinMarketCap {
    val coinInfo = data.coinInfo
    val rawData = data.raw?.get(currency)
    val displayData = data.display?.get(currency)

    return CoinMarketCap(
        id = coinInfo.id,
        name = coinInfo.name,
        fullName = coinInfo.fullName,
        symbol = coinInfo.name,
        imageUrl = coinInfo.imageUrl?.let {
            "https://www.cryptocompare.com$it"
        },
        algorithm = coinInfo.algorithm,
        proofType = coinInfo.proofType,
        rating = coinInfo.rating?.let { mapRatingToDomain(it) },
        marketData = rawData?.let { mapRawDataToDomain(it) },
        displayData = displayData?.let { mapDisplayDataToDomain(it) }
    )
}

private fun mapRatingToDomain(rating: RatingInfo): CoinRating {
    return CoinRating(
        weissRating = rating.weiss?.rating,
        technologyRating = rating.weiss?.technologyAdoptionRating,
        marketPerformanceRating = rating.weiss?.marketPerformanceRating
    )
}

private fun mapRawDataToDomain(raw: RawMarketData): MarketData {
    return MarketData(
        price = raw.price ?: 0.0,
        marketCap = raw.mktCap ?: 0.0,
        circulatingSupply = raw.circulatingSupply,
        totalSupply = raw.supply,
        volume24h = raw.volume24Hour ?: 0.0,
        volumeDay = raw.volumeDay ?: 0.0,
        change24h = raw.change24Hour ?: 0.0,
        changePct24h = raw.changePct24Hour ?: 0.0,
        changeDay = raw.changeDay ?: 0.0,
        changePctDay = raw.changePctDay ?: 0.0,
        high24h = raw.high24Hour ?: 0.0,
        low24h = raw.low24Hour ?: 0.0,
        open24h = raw.open24Hour ?: 0.0,
        highDay = raw.highDay ?: 0.0,
        lowDay = raw.lowDay ?: 0.0,
        openDay = raw.openDay ?: 0.0,
        lastUpdate = raw.lastUpdate ?: 0L,
        fromSymbol = raw.fromSymbol ?: "",
        toSymbol = raw.toSymbol ?: ""
    )
}

private fun mapDisplayDataToDomain(display: DisplayMarketData): DisplayData {
    return DisplayData(
        price = display.price ?: "N/A",
        marketCap = display.mktCap ?: "N/A",
        volume24h = display.volume24Hour ?: "N/A",
        change24h = display.change24Hour ?: "N/A",
        changePct24h = display.changePct24Hour ?: "N/A",
        high24h = display.high24Hour ?: "N/A",
        low24h = display.low24Hour ?: "N/A"
    )
}

// Mapper untuk WebSocket streaming
fun mapCoinDataToStreamUpdate(
    data: CoinMarketCapData,
    currency: String
): CryptoStreamUpdate? {
    val rawData = data.raw?.get(currency) ?: return null

    return CryptoStreamUpdate(
        coinId = data.coinInfo.id,
        symbol = data.coinInfo.name,
        price = rawData.price ?: 0.0,
        change = rawData.change24Hour ?: 0.0,
        changePct = rawData.changePct24Hour ?: 0.0,
        volume = rawData.volume24Hour ?: 0.0,
        timestamp = rawData.lastUpdate ?: 0
    )
}

fun mapCoinDataListToStreamUpdates(
    dataList: List<CoinMarketCapData>,
    currency: String
): List<CryptoStreamUpdate> {
    return dataList.mapNotNull { mapCoinDataToStreamUpdate(it, currency) }
}