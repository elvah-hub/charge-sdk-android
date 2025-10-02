package de.elvah.charge.features.sites.ui.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

/**
 * Return formatted time to display in the UI, following this logic:
 *
 * - more than one year: year, months
 * - less than a year: months, days
 * - less than a month: days, hours
 * - less than a day: hours, minutes
 * - less than 5 hours: hours, minutes, seconds
 * - less than an hour: minutes, seconds
 * - less than minute: seconds
 * @return a pair object: first is the formatted result, second is a boolean indicating if time updates are needed.
 */
@OptIn(ExperimentalTime::class)
internal fun formatTimeUntil(
    target: LocalDateTime,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    now: LocalDateTime = Clock.System.now().toLocalDateTime(timeZone),
): Pair<String, Duration?>? {
    if (target < now) return null

    val nowInstant = now.toInstant(timeZone)
    val targetInstant = target.toInstant(timeZone)
    val duration: Duration = targetInstant - nowInstant

    val period = now.date.periodUntil(target.date)

    val totalHours = duration.inWholeHours
    val hours = totalHours % 24
    val totalMinutes = duration.inWholeMinutes
    val minutes = totalMinutes % 60
    val seconds = duration.inWholeSeconds % 60
    val days = duration.inWholeDays

    // TODO: extract strings resources
    return when {
        period.years > 0 && period.months == 0 -> {
            Pair(
                first = "${period.years} year",
                second = null,
            )
        }

        period.years > 0 -> {
            Pair(
                first = listOfNotNull(
                    "${period.years} year",
                    "${period.months} months".takeIf { period.months > 0 },
                ).joinToString(", "),
                second = null,
            )
        }


        period.months > 0 -> {
            Pair(
                listOfNotNull(
                    "${period.months} month",
                    "${period.days} days".takeIf { period.days > 0 },
                ).joinToString(", "),
                second = null,
            )
        }

        days > 0 -> {
            Pair(
                listOfNotNull(
                    "$days day",
                    "$hours hours".takeIf { hours > 0 },
                ).joinToString(", "),
                second = 1.hours,
            )
        }

        hours > 0 -> {
            Pair(
                first = listOfNotNull(
                    "$hours hours",
                    "$minutes mins".takeIf { minutes > 0 },
                ).joinToString(", "),
                second = 1.minutes,
            )
        }

        minutes > 0 -> {
            Pair(
                first = listOfNotNull(
                    "$minutes mins",
                    "$seconds secs".takeIf { seconds > 0 },
                ).joinToString(" "),
                second = 1.seconds,
            )
        }

        else -> {
            Pair(
                first = "$seconds secs",
                second = 1.seconds,
            )
        }
    }
}
