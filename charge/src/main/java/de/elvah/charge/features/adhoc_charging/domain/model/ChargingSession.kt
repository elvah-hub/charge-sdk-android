package de.elvah.charge.features.adhoc_charging.domain.model

import de.elvah.charge.platform.simulator.data.repository.SessionStatus

class ChargingSession(
    val evseId: String,
    val status: String,
    val status1: SessionStatus,
    val consumption: Double,
    val duration: Int,
)
