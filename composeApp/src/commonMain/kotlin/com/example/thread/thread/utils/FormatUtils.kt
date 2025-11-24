package com.example.thread.thread.utils

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Format double untuk harga crypto
 * Multiplatform compatible
 */
fun Double.formatPrice(): String {
    return when {
        this >= 1000 -> this.formatDecimals(2)
        this >= 1 -> this.formatDecimals(4)
        else -> this.formatDecimals(6)
    }
}

/**
 * Format double untuk persentase
 * Multiplatform compatible
 */
fun Double.formatPercent(): String {
    return abs(this).formatDecimals(2)
}

/**
 * Extension function untuk format decimal
 * Pure Kotlin - support semua platform
 */
fun Double.formatDecimals(decimals: Int): String {
    if (this.isNaN() || this.isInfinite()) return "0"

    val multiplier = 10.0.pow(decimals)
    val rounded = (this * multiplier).roundToInt() / multiplier

    // Build string manually
    val integerPart = rounded.toInt()
    val decimalPart = abs(rounded - integerPart)

    if (decimals == 0) {
        return integerPart.toString()
    }

    // Calculate decimal digits
    val decimalDigits = (decimalPart * multiplier).roundToInt()
    val decimalString = decimalDigits.toString().padStart(decimals, '0')

    return "$integerPart.$decimalString"
}

/**
 * Format dengan thousand separator
 * Contoh: 45000.50 -> "45,000.50"
 */
fun Double.formatWithSeparator(decimals: Int = 2): String {
    val formatted = this.formatDecimals(decimals)
    val parts = formatted.split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1] else ""

    // Add thousand separator
    val withSeparator = integerPart.reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()

    return if (decimalPart.isNotEmpty()) {
        "$withSeparator.$decimalPart"
    } else {
        withSeparator
    }
}
