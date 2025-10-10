package de.elvah.charge.features.sites.domain.extension

import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock.System
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun List<ScheduledPricing.TimeSlot>.getSlotAtTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    dateTime: LocalDateTime = System.now().toLocalDateTime(timeZone),
): ScheduledPricing.TimeSlot? {
    return find { timeSlot ->
        val from = timeSlot.from.timeSlotToLocalDateTime() ?: return@find false
        val to = timeSlot.to.timeSlotToLocalDateTime() ?: return@find false

        dateTime.time >= from.time && dateTime.time < to.time
    }
}

/**
 * This method convert the times received from pricing-schedule (with format HH:mm:ss)
 * to the device local time zone
 */
@OptIn(ExperimentalTime::class)
internal fun String.timeSlotToLocalDateTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): LocalDateTime? {
    val now = System.now().toLocalDateTime(timeZone)

    val (hour, minute, second) = this.split(":")
        .map { it.toInt() }

    val utc = LocalDateTime(
        year = now.year,
        month = now.month,
        day = now.day,
        hour = hour,
        minute = minute,
        second = second,
        nanosecond = 0,
    )

    val localTimeZone = utc.toInstant(timeZone)
    return localTimeZone.toLocalDateTime(timeZone)
}
