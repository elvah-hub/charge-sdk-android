package de.elvah.charge.features.sites.ui.model

import de.elvah.charge.features.sites.domain.model.ChargePointAvailability

internal data class ChargePointUI(
    val shortenedEvseId: String,
    val availability: ChargePointAvailability,
    val pricePerKwh: Double,
    val previousPricePerKwh: Double?,
    val energyValue: Float?,
)

internal data class SignedChargePointUI(
    val evseId: String,
    val pricePerKwh: Double,
    val energyType: String,
    val energyValue: Int,
    val signedOffer: String,
)
