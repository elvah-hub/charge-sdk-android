package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

data class ActiveChargingSessionUI(
    val evseId: String,
    val status: String,
    val consumption: Double,
    val duration: Int,
    val cpoLogo: String,
    val error: Boolean
)
