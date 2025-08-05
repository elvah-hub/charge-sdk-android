package de.elvah.charge.features.sites.ui.model


internal data class DealUI(
    val id: String,
    val cpoName: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val pricePerKw: Double,
    val campaignEnd: String,
    val chargePoints: List<ChargePointUI>,
)
