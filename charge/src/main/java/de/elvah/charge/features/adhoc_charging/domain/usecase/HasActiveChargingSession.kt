package de.elvah.charge.features.adhoc_charging.domain.usecase


internal class HasActiveChargingSession(
    private val getActiveChargingSession: GetActiveChargingSession,
) {

    suspend operator fun invoke(): Boolean {
        return getActiveChargingSession().isRight()
    }
}
