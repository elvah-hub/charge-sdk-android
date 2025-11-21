package de.elvah.charge.features.sites.ui.model


internal data class ChargeSiteUI(
    val id: String,
    val cpoName: String,
    val address: AddressUI,
    val lat: Double,
    val lng: Double,
    val pricePerKw: Double,
    val campaignEnd: String,
    val chargePoints: List<ChargePointUI>,
    val dynamicPricingAvailable: Boolean,
)

internal data class AddressUI(
    val streetAddress: List<String?>,
    val postalCode: String,
    val locality: String
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
