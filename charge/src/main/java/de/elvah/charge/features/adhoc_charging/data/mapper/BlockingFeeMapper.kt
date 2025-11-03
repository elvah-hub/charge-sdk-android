package de.elvah.charge.features.adhoc_charging.data.mapper

import de.elvah.charge.features.adhoc_charging.BlockingFeeProto
import de.elvah.charge.features.adhoc_charging.BlockingFeeTimeSlotProto
import de.elvah.charge.features.sites.domain.model.BlockingFee
import de.elvah.charge.features.sites.domain.model.BlockingFeeTimeSlot

internal val invalidTimeSlot = BlockingFeeTimeSlot(INVALID_TEXT, INVALID_TEXT)

internal val invalidTimeSlots = listOf(
    invalidTimeSlot,
)

internal val invalidBlockingFee = BlockingFee(
    pricePerMinute = invalidPricing,
    startsAfterMinutes = -Int.MIN_VALUE,
    maxAmount = invalidPricing,
    timeSlots = invalidTimeSlots,
    currency = INVALID_TEXT,
)

internal fun BlockingFee?.toProto(): BlockingFeeProto {
    return with(this ?: invalidBlockingFee) {
        val timeSlotList = (timeSlots ?: invalidTimeSlots)

        BlockingFeeProto.newBuilder()
            .setPricePerMinute(pricePerMinute.toProto())
            .setStartsAfterMinutes(startsAfterMinutes)
            .setCurrency(currency)
            .setMaxAmount(maxAmount.toProto())
            .addAllTimeSlots(timeSlotList.map { it.toProto() })
            .build()
    }
}

internal fun BlockingFeeProto.toDomain(): BlockingFee? {
    return pricePerMinute.toDomain()
        ?.let { pricePerMinute ->
            BlockingFee(
                pricePerMinute = pricePerMinute,
                startsAfterMinutes = startsAfterMinutes,
                maxAmount = maxAmount.toDomain(),
                timeSlots = timeSlotsList
                    .mapNotNull { it.toDomain() }
                    .takeIf { it != invalidTimeSlots }
                    ?.takeIf { it.isNotEmpty() },
                currency = currency,
            )
        }
        .takeIf { it != invalidBlockingFee }
}

internal fun BlockingFeeTimeSlot?.toProto(): BlockingFeeTimeSlotProto {
    return with(this ?: invalidTimeSlot) {
        BlockingFeeTimeSlotProto.newBuilder()
            .setStartTime(startTime)
            .setEndTime(endTime)
            .build()
    }
}

internal fun BlockingFeeTimeSlotProto.toDomain(): BlockingFeeTimeSlot? {
    return BlockingFeeTimeSlot(
        startTime = startTime,
        endTime = endTime,
    )
        .takeIf { it != invalidTimeSlot }
}
