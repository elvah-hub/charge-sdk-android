package de.elvah.charge.features.adhoc_charging.ui.model

import de.elvah.charge.features.sites.domain.model.BlockingFeeTimeSlot
import de.elvah.charge.features.sites.domain.model.Pricing

internal data class AdditionalCostsUI(
    val activationFee: Pricing?,
    val blockingFee: Pricing?,
    val blockingFeeMaxPrice: Pricing?,
    val startsAfterMinutes: Int?,
    val timeSlots: List<BlockingFeeTimeSlot>?,
)
