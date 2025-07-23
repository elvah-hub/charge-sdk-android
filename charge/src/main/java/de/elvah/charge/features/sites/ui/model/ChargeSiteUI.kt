package de.elvah.charge.features.sites.ui.model


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

internal data class ChargeBannerRender(
    val id: String,
    val cpoName: String,
    val address: String,
    val location: Location,
    val originalPrice: Double? = null,
    val price: Double,
    val campaignEnd: String,
)

internal data class Location(
    val lat: Double,
    val lng: Double,
)
