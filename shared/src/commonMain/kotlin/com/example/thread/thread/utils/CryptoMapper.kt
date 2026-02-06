package com.example.thread.thread.utils

import com.example.thread.thread.data.remote.response.SpotTickItemResponse
import com.example.thread.thread.data.remote.response.SpotTickResponse
import com.example.thread.thread.domain.model.CryptoTicker
import com.example.thread.thread.domain.model.toException

fun toDomain(dto: SpotTickItemResponse): CryptoTicker {
    val lastPrice = dto.lastTradePrice ?: 0.0
    val dayOpen = dto.currentDayOpen ?: lastPrice

    // Calculate price change
    val priceChange = if (dayOpen > 0 && lastPrice > 0) {
        lastPrice - dayOpen
    } else null

    // Calculate percentage change
    val priceChangePercentage = if (dayOpen > 0 && priceChange != null) {
        (priceChange / dayOpen) * 100
    } else null

    return CryptoTicker(
        market = dto.market,
        instrument = dto.instrument,
        base = dto.base,
        quote = dto.quote,
        lastTradePrice = lastPrice,
        lastUpdateTimestamp = dto.lastUpdateTs,
        lastTradeQuantity = dto.lastTradeQuantity,
        volume24h = dto.currentDayVolume,
        currentDayOpen = dto.currentDayOpen,
        currentDayHigh = dto.currentDayHigh,
        currentDayLow = dto.currentDayLow,
        priceChange24h = priceChange,
        priceChangePercentage24h = priceChangePercentage,
        formattedPrice = formatPrice(lastPrice, dto.quote),
        formattedChange = priceChangePercentage?.toPercentageString(2),
        formattedVolume = dto.currentDayVolume?.toVolumeString(2),
        formattedLastUpdate = dto.lastUpdateTs.toRelativeTimeString(),
        totalTrades24h = dto.currentDayTotalTrades
    )
}

/**
 * Map response DTO to list of domain models
 */
fun toDomainList(response: SpotTickResponse): List<CryptoTicker> {
    // Check for API error
    if (response.error != null) {
        throw response.error.toException()
    }

    // Get both mapped and unmapped pairs
    val mappedPairs = response.data?.mappedPairs ?: emptyList()
    val unmappedPairs = response.data?.unmappedPairs ?: emptyList()

    // Combine and limit to 10 items
    return (mappedPairs + unmappedPairs)
        .take(10)
        .map { toDomain(it) }
}

private fun formatPrice(price: Double, currency: String): String {
    val symbol = when (currency.uppercase()) {
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        "JPY" -> "¥"
        "BTC" -> "₿"
        "ETH" -> "Ξ"
        else -> currency
    }

    return price.toPriceString(
        currency = symbol,
        minDecimals = 2,
        maxDecimals = 8
    )
}