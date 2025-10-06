package de.elvah.charge.features.sites.ui.utils

import android.content.Context
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
    context: Context,
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
    val hours = (totalHours % 24).toInt()
    val totalMinutes = duration.inWholeMinutes
    val minutes = (totalMinutes % 60).toInt()
    val seconds = (duration.inWholeSeconds % 60).toInt()
    val days = duration.inWholeDays.toInt()
    val separator = " "

    return when {
        period.years > 0 && period.months == 0 -> {
            Pair(
                first = context.resources.getQuantityString(
                    de.elvah.charge.R.plurals.generic_year,
                    period.years,
                ),
                second = null,
            )
        }

        period.years > 0 -> {
            val yearText = context.resources.getQuantityString(
                de.elvah.charge.R.plurals.generic_year,
                period.years,
            )

            val monthText = context.resources.getQuantityString(
                de.elvah.charge.R.plurals.generic_month,
                period.months,
            )

            Pair(
                first = listOfNotNull(
                    yearText,
                    monthText.takeIf { period.months > 0 },
                ).joinToString(separator),
                second = null,
            )
        }

        period.months > 0 -> {
            val monthText = context.resources.getQuantityString(
                de.elvah.charge.R.plurals.generic_month,
                period.months,
            )

            val dayText = context.resources.getQuantityString(
                de.elvah.charge.R.plurals.generic_day,
                period.days,
            )

            Pair(
                listOfNotNull(
                    monthText,
                    dayText.takeIf { period.days > 0 },
                ).joinToString(separator),
                second = null,
            )
        }

        days > 0 -> {
            val dayText = context.resources.getQuantityString(
                de.elvah.charge.R.plurals.generic_day,
                days,
            )

            val hourText = context.resources.getString(
                de.elvah.charge.R.string.generic_hour_abbreviation,
                hours.toString(),
            )

            Pair(
                listOfNotNull(
                    dayText,
                    hourText.takeIf { hours > 0 },
                ).joinToString(separator),
                second = 1.hours,
            )
        }

        hours > 0 -> {
            val hourText = context.resources.getString(
                de.elvah.charge.R.string.generic_hour_abbreviation,
                hours.toString(),
            )

            val minuteText = context.resources.getString(
                de.elvah.charge.R.string.generic_minutes_abbreviation,
                minutes.toString(),
            )

            Pair(
                first = listOfNotNull(
                    hourText,
                    minuteText.takeIf { minutes > 0 },
                ).joinToString(separator),
                second = 1.minutes,
            )
        }

        minutes > 0 -> {
            val minuteText = context.resources.getString(
                de.elvah.charge.R.string.generic_minutes_abbreviation,
                minutes.toString(),
            )

            val secondText = context.resources.getString(
                de.elvah.charge.R.string.generic_seconds_abbreviation,
                seconds.toString(),
            )

            Pair(
                first = listOfNotNull(
                    minuteText,
                    secondText.takeIf { seconds > 0 },
                ).joinToString(separator),
                second = 1.seconds,
            )
        }

        else -> {
            Pair(
                first = context.resources.getString(
                    de.elvah.charge.R.string.generic_seconds_abbreviation,
                    seconds.toString(),
                ),
                second = 1.seconds,
            )
        }
    }
}
