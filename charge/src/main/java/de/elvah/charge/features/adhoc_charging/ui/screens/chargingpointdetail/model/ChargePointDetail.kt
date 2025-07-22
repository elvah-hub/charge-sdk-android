package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.model

class ChargePointDetail(
    val chargingPoint: String,
    val type: String,
    val offer: Offer,
    val cpoName: String,
    val termsUrl: String,
    val privacyUrl: String,
    val evseId: String,
    val energy: String,
    val signedOffer: String
) {
    class Offer(
        val current: Double,
        val old: Double?,
    )
}
