package de.elvah.charge.features.sites.ui.model

import de.elvah.charge.features.deals.ui.model.ChargePointUI

internal data class ChargeSiteUI(
    val id: String,
    val cpoName: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val pricePerKw: Double,
    val campaignEnd: String,
    val chargePoints: List<ChargePointUI>,
)
