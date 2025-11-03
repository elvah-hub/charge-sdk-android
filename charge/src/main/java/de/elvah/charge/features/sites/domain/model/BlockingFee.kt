package de.elvah.charge.features.sites.domain.model

public data class BlockingFee(
    val pricePerMinute: Pricing,
    val startsAfterMinutes: Int,
    val maxAmount: Pricing?,
    val timeSlots: List<BlockingFeeTimeSlot>?,
    val currency: String,
)

public data class BlockingFeeTimeSlot(
    val startTime: String,
    val endTime: String,
)
