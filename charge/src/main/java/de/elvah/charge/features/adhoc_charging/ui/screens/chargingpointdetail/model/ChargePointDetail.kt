package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.model

class ChargePointDetail(
    val chargingPoint: String,
    val type: String,
    val price: Price,
    val cpoName: String,
    val termsUrl: String,
    val privacyUrl: String,
    val evseId: String,
    val energy: String,
    val signedOffer: String
) {
    class Price(
        val current: String,
        val old: String,
    )
}