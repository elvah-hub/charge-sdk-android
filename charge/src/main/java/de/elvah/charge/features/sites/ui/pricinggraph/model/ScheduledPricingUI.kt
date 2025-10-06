package de.elvah.charge.features.sites.ui.pricinggraph.model

import kotlinx.datetime.LocalDateTime

internal data class ScheduledPricingUI(
    val dailyPricing: DailyPricingUI,
    val standardPrice: PriceUI
) {
    data class DailyPricingUI(
        val yesterday: DayUI,
        val today: DayUI,
        val tomorrow: DayUI
    )

    data class DayUI(
        val lowestPrice: PriceUI,
        val trend: String?,
        val timeSlots: List<TimeSlotUI>
    )

    data class TimeSlotUI(
        val isDiscounted: Boolean,
        val price: PriceUI,
        val from: LocalDateTime,
        val fromText: String,
        val to: LocalDateTime,
        val toText: String,
    )

    data class PriceUI(
        val energyPricePerKWh: Double,
        val baseFee: Int?,
        val currency: String,
        val blockingFee: BlockingFeeUI?
    ) {
        data class BlockingFeeUI(
            val pricePerMinute: Int,
            val startsAfterMinutes: Int
        )
    }
}
