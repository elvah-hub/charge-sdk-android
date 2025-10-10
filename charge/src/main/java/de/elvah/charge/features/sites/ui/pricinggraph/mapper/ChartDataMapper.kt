package de.elvah.charge.features.sites.ui.pricinggraph.mapper

import de.elvah.charge.features.sites.ui.pricinggraph.model.ScheduledPricingUI
import de.elvah.charge.platform.ui.components.graph.line.DailyPricingData
import de.elvah.charge.platform.ui.components.graph.line.PriceOffer
import de.elvah.charge.platform.ui.components.graph.line.TimeRange
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

internal fun ScheduledPricingUI.toChartData(): List<DailyPricingData> {
    val today = LocalDate.now()

    return listOf(
        dailyPricing.yesterday.toChartData(
            date = today.minusDays(1),
            currency = standardPrice.currency
        ),
        dailyPricing.today.toChartData(
            date = today,
            currency = standardPrice.currency
        ),
        dailyPricing.tomorrow.toChartData(
            date = today.plusDays(1),
            currency = standardPrice.currency
        )
    )
}

private fun ScheduledPricingUI.DayUI.toChartData(
    date: LocalDate,
    currency: String
): DailyPricingData {
    // Find the regular price from non-discounted time slots or use the lowest price as fallback
    val regularPrice = timeSlots.firstOrNull { !it.isDiscounted }?.price?.energyPricePerKWh
        ?: lowestPrice.energyPricePerKWh

    // Convert discounted time slots to price offers
    val offers = timeSlots
        .filter { it.isDiscounted }
        .mapNotNull { timeSlot ->
            val timeRange = parseTimeRange(timeSlot.from, timeSlot.to)
            timeRange?.let { range ->
                PriceOffer(
                    timeRange = range,
                    discountedPrice = timeSlot.price.energyPricePerKWh
                )
            }
        }

    return DailyPricingData(
        date = date,
        regularPrice = regularPrice,
        offers = offers,
        currency = currency
    )
}

private fun parseTimeRange(from: String, to: String): TimeRange? {
    return try {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val startTime = LocalTime.parse(from, formatter)
        val endTime = LocalTime.parse(to, formatter)

        TimeRange(startTime = startTime, endTime = endTime)
    } catch (e: DateTimeParseException) {
        // Try alternative format HH:MM or H:MM
        try {
            val startTime = parseTimeFlexible(from)
            val endTime = parseTimeFlexible(to)
            TimeRange(startTime = startTime, endTime = endTime)
        } catch (e2: Exception) {
            null // Return null if unable to parse
        }
    }
}

private fun parseTimeFlexible(timeString: String): LocalTime {
    return when {
        timeString.contains(":") -> {
            val parts = timeString.split(":")
            LocalTime.of(parts[0].toInt(), parts[1].toInt())
        }

        timeString.length <= 2 -> {
            LocalTime.of(timeString.toInt(), 0)
        }

        else -> {
            // Assume format like 1430 for 14:30
            val hour = timeString.substring(0, timeString.length - 2).toInt()
            val minute = timeString.substring(timeString.length - 2).toInt()
            LocalTime.of(hour, minute)
        }
    }
}
