package de.elvah.charge.features.sites.ui.model


internal data class ChargePointUI(
    val evseId: String,
    val pricePerKwh: Double,
    val energyType: String,
    val energyValue: Int,
)

internal data class SignedChargePointUI(
    val evseId: String,
    val pricePerKwh: Double,
    val energyType: String,
    val energyValue: Int,
    val signedOffer: String,
)
