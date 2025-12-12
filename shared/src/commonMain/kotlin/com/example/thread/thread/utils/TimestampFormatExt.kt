package com.example.thread.thread.utils

import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


@OptIn(ExperimentalTime::class)
fun Long.toFormattedDateTime(
    includeTime: Boolean = true,
    includeSeconds: Boolean = false
): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val year = dateTime.year
    val month = dateTime.monthNumber.toString().padStart(2, '0')
    val day = dateTime.dayOfMonth.toString().padStart(2, '0')

    val date = "$year-$month-$day"

    if (!includeTime) return date

    val hour = dateTime.hour.toString().padStart(2, '0')
    val minute = dateTime.minute.toString().padStart(2, '0')

    return if (includeSeconds) {
        val second = dateTime.second.toString().padStart(2, '0')
        "$date $hour:$minute:$second"
    } else {
        "$date $hour:$minute"
    }
}

/**
 * Get relative time string (e.g., "2 hours ago")
 */
@OptIn(ExperimentalTime::class)
fun Long.toRelativeTimeString(): String {
    val now = Clock.System.now().toEpochMilliseconds()
    val diff = now - this

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "$hours hr ago"
        days < 7 -> "$days day${if (days > 1) "s" else ""} ago"
        else -> this.toFormattedDateTime(includeTime = false)
    }
}
