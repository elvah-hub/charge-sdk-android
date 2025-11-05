package de.elvah.charge.public_api.model

public class ChargeSession(
    public val evseId: String,
    public val status: SessionStatus,
    public val consumption: Double,
    public val duration: Int,
)
