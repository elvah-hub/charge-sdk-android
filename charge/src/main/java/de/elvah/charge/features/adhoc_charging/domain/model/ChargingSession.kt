package de.elvah.charge.features.adhoc_charging.domain.model

class ChargingSession(
    val evseId: String,
    val status: String,
    val consumption: Double,
    val duration: Int,
)
