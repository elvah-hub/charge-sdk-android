package de.elvah.charge.features.adhoc_charging.ui.mapper

import de.elvah.charge.features.adhoc_charging.ui.model.AdditionalCostsUI
import de.elvah.charge.features.sites.domain.model.AdditionalCosts
import de.elvah.charge.features.sites.domain.model.BlockingFeeTimeSlot
import de.elvah.charge.features.sites.ui.utils.HOUR_MINUTE_FORMAT

internal fun AdditionalCosts.toUI(): AdditionalCostsUI? = AdditionalCostsUI(
    activationFee = baseFee,
    blockingFee = blockingFee?.pricePerMinute,
    blockingFeeMaxPrice = blockingFee?.maxAmount,
    startsAfterMinutes = blockingFee?.startsAfterMinutes,
    timeSlots = blockingFee?.timeSlots
        ?.filter { it.startTime.isNotEmpty() && it.endTime.isNotEmpty() }
        ?.mapNotNull {
            it.startTime.hoursAndMinutesText?.let { startTime ->
                it.endTime.hoursAndMinutesText?.let { endTime ->
                    BlockingFeeTimeSlot(
                        startTime = startTime,
                        endTime = endTime,
                    )
                }
            }
        },
)
    .takeIf {
        it.activationFee != null ||
                it.blockingFee != null
    }

private val String.hoursAndMinutesText: String?
    get() = runCatching {
        val (hour, minute, _) = this.split(":")
            .map { it.toInt() }

        return HOUR_MINUTE_FORMAT.format(hour, minute)
    }.getOrNull()
