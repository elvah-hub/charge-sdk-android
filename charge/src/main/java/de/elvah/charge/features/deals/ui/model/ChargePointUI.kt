package de.elvah.charge.features.deals.ui.model


internal data class ChargePointUI(
    val evseId: String,
    val pricePerKwh: Double,
    val energyType: String,
    val energyValue: Int,
    val signedDeal: String,
)
