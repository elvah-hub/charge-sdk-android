package de.elvah.charge.platform.ui.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun formatTimeUntil(
    target: LocalDateTime,
    onlyDays: Boolean = false
): String? {
    val timeZone = TimeZone.currentSystemDefault()
    val now = Clock.System.now()
        .toLocalDateTime(timeZone)

    if (now >= target) return null

    val nowInstant = now.toInstant(timeZone)
    val targetInstant = target.toInstant(timeZone)
    val duration: Duration = targetInstant - nowInstant

    if (onlyDays) {
        val days = duration.inWholeDays
        val hours = (duration - days.days).inWholeHours
        return buildString {
            // TODO: extract string resource
            append("$days day${if (days != 1L) "s" else ""}")
            if (hours > 0) append(" ${hours}h")
        }
    }

    val period = now.date.periodUntil(target.date)

    return when {
        duration.inWholeDays < 1 -> {
            val hours = duration.inWholeHours
            val minutes = (duration - hours.hours).inWholeMinutes
            "${hours}h ${minutes}min" // TODO: extract string resource
        }

        else -> {
            val years = period.years
            val months = period.months
            val days = period.days
            val hours = (duration - duration.inWholeDays.days).inWholeHours

            buildString {
                // TODO: extract string resources
                if (years > 0) append("$years year${if (years > 1) "s" else ""}, ")
                if (months > 0) append("$months month${if (months > 1) "s" else ""}, ")
                if (days > 0) append("$days day${if (days > 1) "s" else ""}, ")
                if (hours > 0) append("$hours h")
            }
                .trim()
                .trimEnd(',')
        }
    }
}
