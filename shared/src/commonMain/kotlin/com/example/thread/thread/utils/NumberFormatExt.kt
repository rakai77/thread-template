package com.example.thread.thread.utils

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.floor
import kotlin.math.log10

fun Double.toFormattedString(
    minDecimals: Int = 2,
    maxDecimals: Int = 8,
    addPositiveSign: Boolean = false
): String {
    if (this.isNaN() || this.isInfinite()) return "0"

    val isNegative = this < 0
    val absoluteValue = abs(this)

    // Handle scientific notation manually
    val plainString = if (absoluteValue !in 1e-6..<1e9) {
        // Convert scientific notation to plain string
        scientificToPlainString(absoluteValue, maxDecimals)
    } else {
        // Round to max decimals
        val multiplier = 10.0.pow(maxDecimals)
        val rounded = round(absoluteValue * multiplier) / multiplier
        rounded.toString()
    }

    // Split integer and decimal parts
    val parts = plainString.split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1] else ""

    // Adjust decimal places
    val adjustedDecimal = when {
        decimalPart.length < minDecimals -> {
            decimalPart.padEnd(minDecimals, '0')
        }
        decimalPart.length > maxDecimals -> {
            decimalPart.substring(0, maxDecimals)
        }
        else -> decimalPart
    }.trimEnd('0').let {
        // Ensure at least minDecimals
        if (it.length < minDecimals) it.padEnd(minDecimals, '0') else it
    }

    // Build final string
    val sign = when {
        isNegative -> "-"
        addPositiveSign -> "+"
        else -> ""
    }

    return if (adjustedDecimal.isEmpty() || adjustedDecimal.all { it == '0' }) {
        "$sign$integerPart"
    } else {
        "$sign$integerPart.$adjustedDecimal"
    }
}

/**
 * Convert scientific notation to plain string (KMP-compatible)
 */
private fun scientificToPlainString(value: Double, maxDecimals: Int): String {
    if (value == 0.0) return "0"

    // Get the exponent
    val exponent = floor(log10(value)).toInt()

    // Get mantissa
    val mantissa = value / 10.0.pow(exponent)

    return when {
        exponent >= 0 -> {
            // Large numbers: shift decimal point right
            val intPart = (mantissa * 10.0.pow(exponent)).toLong()
            val remaining = value - intPart

            if (remaining < 1e-10) {
                intPart.toString()
            } else {
                val decimalPart = (remaining * 10.0.pow(maxDecimals)).toLong()
                val decimalStr = decimalPart.toString().padStart(maxDecimals, '0')
                "$intPart.$decimalStr"
            }
        }
        else -> {
            // Small numbers: shift decimal point left
            val zeros = abs(exponent) - 1
            val significantDigits = (mantissa * 10.0.pow(maxDecimals)).toLong()
            val decimalStr = "0".repeat(zeros) + significantDigits.toString()
            "0.$decimalStr"
        }
    }
}

/**
 * Simpler alternative: Format double without scientific notation
 * More reliable for KMP
 */
fun Double.toPlainString(decimals: Int = 8): String {
    if (this.isNaN()) return "NaN"
    if (this.isInfinite()) return if (this > 0) "Infinity" else "-Infinity"
    if (this == 0.0) return "0"

    val isNegative = this < 0
    val abs = abs(this)

    // Split into integer and fractional parts
    val integerPart = floor(abs).toLong()
    val fractionalPart = abs - integerPart

    // Format fractional part
    val multiplier = 10.0.pow(decimals)
    val fractionalDigits = round(fractionalPart * multiplier).toLong()

    val sign = if (isNegative) "-" else ""

    return if (fractionalDigits == 0L) {
        "$sign$integerPart"
    } else {
        val fracStr = fractionalDigits.toString().padStart(decimals, '0').trimEnd('0')
        "$sign$integerPart.$fracStr"
    }
}

/**
 * Format percentage with sign
 */
fun Double.toPercentageString(decimals: Int = 2): String {
    val formatted = this.toPlainString(decimals)
    val sign = if (this >= 0) "+" else ""

    // Trim to desired decimals
    val parts = formatted.split(".")
    val decimal = if (parts.size > 1) {
        parts[1].take(decimals).padEnd(decimals, '0')
    } else {
        "0".repeat(decimals)
    }

    return "$sign${parts[0]}.${decimal}%"
}

/**
 * Format price with currency symbol
 */
fun Double.toPriceString(
    currency: String = "$",
    minDecimals: Int = 2,
    maxDecimals: Int = 8
): String {
    // Determine decimal places based on price magnitude
    val decimalsToUse = when {
        this >= 1000 -> 2
        this >= 1 -> 4
        this >= 0.01 -> 6
        else -> 8
    }

    val actualMaxDecimals = decimalsToUse.coerceAtMost(maxDecimals)
    val plainStr = this.toPlainString(actualMaxDecimals)

    // Parse and format
    val parts = plainStr.split(".")
    val intPart = parts[0]
    val decPart = if (parts.size > 1) {
        parts[1].take(actualMaxDecimals).padEnd(minDecimals.coerceAtMost(actualMaxDecimals), '0')
    } else {
        "0".repeat(minDecimals.coerceAtMost(actualMaxDecimals))
    }

    return if (decPart.all { it == '0' } && minDecimals == 0) {
        "$currency$intPart"
    } else {
        "$currency$intPart.$decPart"
    }
}

/**
 * Format volume with K, M, B suffixes
 */
fun Double.toVolumeString(decimals: Int = 2): String {
    return when {
        this >= 1_000_000_000 -> {
            val value = this / 1_000_000_000
            "${value.toPlainString(decimals).take(decimals + 2)}B"
        }
        this >= 1_000_000 -> {
            val value = this / 1_000_000
            "${value.toPlainString(decimals).take(decimals + 2)}M"
        }
        this >= 1_000 -> {
            val value = this / 1_000
            "${value.toPlainString(decimals).take(decimals + 2)}K"
        }
        else -> {
            this.toPlainString(decimals)
        }
    }
}

/**
 * Add thousand separators (KMP-compatible)
 */
fun Double.toFormattedStringWithSeparator(
    decimals: Int = 2,
    separator: String = ","
): String {
    val plainStr = this.toPlainString(decimals)
    val isNegative = plainStr.startsWith("-")
    val withoutSign = plainStr.removePrefix("-")

    val parts = withoutSign.split(".")
    val integerPart = parts[0]
    val decimalPart = if (parts.size > 1) parts[1] else ""

    // Add separators to integer part (from right to left)
    val withSeparators = integerPart.reversed()
        .chunked(3)
        .joinToString(separator)
        .reversed()

    val sign = if (isNegative) "-" else ""
    val decimal = if (decimalPart.isNotEmpty()) ".$decimalPart" else ""

    return "$sign$withSeparators$decimal"
}

/**
 * Round to significant figures (KMP-compatible)
 */
fun Double.toSignificantFigures(figures: Int): String {
    if (this == 0.0) return "0"

    val abs = abs(this)
    val exponent = floor(log10(abs)).toInt()
    val shift = figures - exponent - 1

    val multiplier = 10.0.pow(shift)
    val rounded = round(this * multiplier) / multiplier

    return rounded.toPlainString(shift.coerceAtLeast(0))
}
