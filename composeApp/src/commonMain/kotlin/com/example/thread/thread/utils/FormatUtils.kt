import kotlin.math.abs
import kotlin.math.round

fun Double.toRupiahFormat(): String {
    val absValue = abs(this)
    val sign = if (this < 0) "-" else ""

    return when {
        absValue >= 1_000_000_000_000 -> {
            val value = absValue / 1_000_000_000_000
            "${sign}Rp ${value.roundToDecimals(2)} T"
        }
        absValue >= 1_000_000_000 -> {
            val value = absValue / 1_000_000_000
            "${sign}Rp ${value.roundToDecimals(2)} M"
        }
        absValue >= 1_000_000 -> {
            val value = absValue / 1_000_000
            "${sign}Rp ${value.roundToDecimals(2)} Jt"
        }
        absValue >= 1_000 -> {
            val value = absValue / 1_000
            "${sign}Rp ${value.roundToDecimals(2)} Rb"
        }
        else -> {
            "${sign}Rp ${absValue.roundToDecimals(2)}"
        }
    }
}

// ✅ Versi tanpa pow - menggunakan when expression
fun Double.roundToDecimals(decimals: Int): String {
    val multiplier = when (decimals) {
        0 -> 1.0
        1 -> 10.0
        2 -> 100.0
        3 -> 1000.0
        4 -> 10000.0
        else -> 100.0 // default 2 decimals
    }

    val rounded = round(this * multiplier) / multiplier
    return rounded.toFormattedString(decimals)
}

// ✅ Format dengan pemisah ribuan dan desimal
fun Double.toFormattedString(decimals: Int): String {
    // Split integer dan decimal part
    val integerPart = this.toLong()
    val decimalPart = ((this - integerPart) * 100).toLong() // untuk 2 decimals

    // Format integer dengan titik pemisah ribuan
    val formattedInteger = integerPart.toThousandSeparator()

    // Gabungkan dengan decimal jika perlu
    return if (decimals > 0) {
        val decimalStr = decimalPart.toString().padStart(2, '0')
        "$formattedInteger,$decimalStr"
    } else {
        formattedInteger
    }
}

// ✅ Pemisah ribuan untuk Long
fun Long.toThousandSeparator(): String {
    val str = this.toString()
    return str.reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()
}

// ✅ Versi lebih simple tanpa pemisah ribuan
fun Double.toRupiahSimple(): String {
    val absValue = abs(this)
    val sign = if (this < 0) "-" else ""

    return when {
        absValue >= 1_000_000_000_000 -> {
            val value = (absValue / 1_000_000_000_000 * 100).toLong() / 100.0
            "${sign}Rp ${value} T"
        }
        absValue >= 1_000_000_000 -> {
            val value = (absValue / 1_000_000_000 * 100).toLong() / 100.0
            "${sign}Rp ${value} M"
        }
        absValue >= 1_000_000 -> {
            val value = (absValue / 1_000_000 * 100).toLong() / 100.0
            "${sign}Rp ${value} Jt"
        }
        absValue >= 1_000 -> {
            val value = (absValue / 1_000 * 100).toLong() / 100.0
            "${sign}Rp ${value} Rb"
        }
        else -> {
            val value = (absValue * 100).toLong() / 100.0
            "${sign}Rp ${value}"
        }
    }
}

// Format persen
fun Double.toPercentFormat(): String {
    val rounded = (this * 100 * 100).toLong() / 100.0
    val sign = if (this >= 0) "+" else ""
    return "$sign$rounded%"
}

// Format change dengan warna
fun Double.toChangeFormat(): String {
    val rounded = (this * 100).toLong() / 100.0
    val sign = if (this >= 0) "+" else ""
    return "$sign$rounded"
}