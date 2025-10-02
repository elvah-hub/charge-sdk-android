package de.elvah.charge.features.sites.domain.extension

import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.domain.model.ScheduledPricing.Price
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock.System
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
internal fun List<ScheduledPricing.TimeSlot>.getSlotAtTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    dateTime: LocalDateTime = System.now().toLocalDateTime(timeZone),
): ScheduledPricing.TimeSlot? {
    val currentTime = dateTime.time

    return find { timeSlot ->
        val from = timeSlot.from.timeSlotToLocalDateTime() ?: return@find false
        val to = timeSlot.to.timeSlotToLocalDateTime() ?: return@find false

        currentTime >= from.time && currentTime < to.time
    }
}

// TODO: check if we can extract this to be reused
// Convert time strings with format "HH:mm:ss" to a local date time
@OptIn(ExperimentalTime::class)
private fun String.timeSlotToLocalDateTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    dateTime: LocalDateTime = System.now().toLocalDateTime(timeZone),
): LocalDateTime? {
    val (hour, minute, second) = this.split(":")
        .map { it.toInt() }

    return LocalDateTime(
        year = dateTime.year,
        month = dateTime.month,
        day = dateTime.day,
        hour = hour,
        minute = minute,
        second = second,
        nanosecond = 0,
    )
}

internal data class TimeSlotUI(
    val isDiscounted: Boolean,
    val price: Price,
    val from: LocalDateTime,
    val to: LocalDateTime,
)

@OptIn(ExperimentalTime::class)
internal fun ScheduledPricing.TimeSlot.toUI(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    defaultTime: LocalDateTime = System.now().toLocalDateTime(timeZone),
): TimeSlotUI {
    return TimeSlotUI(
        isDiscounted = isDiscounted,
        price = price,
        from = from.timeSlotToLocalDateTime() ?: defaultTime,
        to = to.timeSlotToLocalDateTime() ?: defaultTime,
    )
}
