package de.elvah.charge.features.adhoc_charging.domain.model

import de.elvah.charge.platform.simulator.data.repository.SessionStatus

public class ChargingSession(
    public val evseId: String,
    public val status: SessionStatus,
    public val consumption: Double,
    public val duration: Int,
)
