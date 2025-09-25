package de.elvah.charge.features.sites.ui.model

import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Price

internal data class ChargePointUI(
    val shortenedEvseId: String,
    val availability: ChargePointAvailability,
    val pricePerKwh: Price,
    val energyValue: Float?,
)

internal data class SignedChargePointUI(
    val evseId: String,
    val pricePerKwh: Double,
    val energyType: String,
    val energyValue: Int,
    val signedOffer: String,
)
