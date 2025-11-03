package de.elvah.charge.features.sites.domain.model

import de.elvah.charge.features.sites.domain.model.ChargeSite.ChargePoint.Offer.Price

internal data class ScheduledPricing(
    val dailyPricing: DailyPricing,
    val standardPrice: Price,
) {
    data class DailyPricing(
        val yesterday: Day,
        val today: Day,
        val tomorrow: Day
    )

    data class Day(
        val lowestPrice: Price,
        val trend: String?,
        val timeSlots: List<TimeSlot>
    )

    data class TimeSlot(
        val isDiscounted: Boolean,
        val price: Price,
        val from: String,
        val to: String
    )
}
