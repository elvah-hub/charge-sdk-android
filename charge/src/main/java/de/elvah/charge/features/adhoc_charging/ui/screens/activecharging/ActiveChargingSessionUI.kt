package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import de.elvah.charge.platform.simulator.data.repository.SessionStatus

internal data class ActiveChargingSessionUI(
    val evseId: String,
    val status: SessionStatus,
    val consumption: Double,
    val duration: Int,
    val cpoLogo: String,
    val error: Boolean
)
