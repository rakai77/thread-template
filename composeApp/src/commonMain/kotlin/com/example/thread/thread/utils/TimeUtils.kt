package com.example.thread.thread.utils

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

/**
 * Get current timestamp in milliseconds (KMP compatible)
 */
@OptIn(ExperimentalTime::class)
fun getCurrentTimeMillis(): Long {
    return kotlin.time.Clock.System.now().toEpochMilliseconds()
}

/**
 * Format timestamp to human readable string
 */
fun Long.formatTimeAgo(): String {
    val now = getCurrentTimeMillis()
    val diff = now - this

    return when {
        diff < 0 -> "Just now"
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        else -> "${diff / 86400_000}d ago"
    }
}

/**
 * Get Instant from timestamp
 */
@OptIn(ExperimentalTime::class)
fun Long.toInstant(): kotlin.time.Instant {
    return Instant.fromEpochMilliseconds(this)
}

/**
 * Format timestamp to date string
 */
@OptIn(ExperimentalTime::class)
fun Long.toDateString(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.dayOfMonth}/${dateTime.monthNumber}/${dateTime.year}"
}

/**
 * Format timestamp to time string
 */
@OptIn(ExperimentalTime::class)
fun Long.toTimeString(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${dateTime.hour.toString().padStart(2, '0')}:${dateTime.minute.toString().padStart(2, '0')}"
}

/**
 * Format timestamp to full date time string
 */
fun Long.toFullDateTimeString(): String {
    return "${toDateString()} ${toTimeString()}"
}
