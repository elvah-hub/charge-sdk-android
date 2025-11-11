package de.elvah.charge.features.adhoc_charging.domain.model

import de.elvah.charge.platform.simulator.data.repository.SessionStatus

internal class ChargeSession(
    val evseId: String,
    val status: SessionStatus,
    val consumption: Double,
    val duration: Int,
)
