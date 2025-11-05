package de.elvah.charge.public_api.mapper


import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargingSessionState
import de.elvah.charge.public_api.model.ChargingSessionState as PublicChargingSessionState

internal fun ChargingSessionState.toPublic(): PublicChargingSessionState {
    return PublicChargingSessionState(
        isSessionRunning = isSessionRunning,
        isSessionSummaryReady = isSessionSummaryReady,
        lastSessionData = lastSessionData?.toPublic(),
    )
}
