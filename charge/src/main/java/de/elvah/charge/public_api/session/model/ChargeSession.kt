package de.elvah.charge.public_api.session.model

import de.elvah.charge.platform.simulator.data.repository.SessionStatus

public class ChargeSession(
    public val evseId: String,
    public val status: SessionStatus, // TODO: reduce status values for public API
    public val consumption: Double,
    public val duration: Int,
)
