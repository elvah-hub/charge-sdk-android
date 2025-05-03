package de.elvah.charge.features.deals.domain.model

internal class Deal(
    val id: String,
    val operatorName: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val chargePoints: List<ChargePoint>,
    val pricePerKwh: Double,
    val campaignEnd: String,
)

internal class ChargePoint(
    val evseId: String,
    val pricePerKwh: Double,
    val energyType: String,
    val energyValue: Int,
    val signedDeal: String,
)