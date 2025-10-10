package de.elvah.charge.features.sites.ui.model

import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Pricing
import de.elvah.charge.public_api.model.EvseId

internal data class ChargePointUI(
    val evseId: EvseId,
    val shortenedEvseId: String,
    val availability: ChargePointAvailability,
    val standardPricePerKwh: Pricing,
    val maxPowerInKW: Float?,
    val powerType: String?,
)

internal data class SignedChargePointUI(
    val evseId: String,
    val pricePerKwh: Double,
    val energyType: String,
    val energyValue: Int,
    val signedOffer: String,
)
