package de.elvah.charge.public_api.session.model

public class ChargeSession(
    public val evseId: String,
    public val status: String,
    public val consumption: Double,
    public val duration: Int,
)
