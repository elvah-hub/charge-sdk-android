package de.elvah.charge.features.sites.domain.model

internal data class ScheduledPricing(
    val dailyPricing: DailyPricing,
    val standardPrice: Price
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

    data class Price(
        val energyPricePerKWh: Double,
        val baseFee: Int?,
        val currency: String,
        val blockingFee: BlockingFee?
    ) {
        data class BlockingFee(
            val pricePerMinute: Int,
            val startsAfterMinutes: Int
        )
    }
}
