package de.elvah.charge.features.adhoc_charging.domain.service.charge

import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession

internal data class ChargingSessionState(
    val isSessionRunning: Boolean,
    val isSessionSummaryReady: Boolean,
    val lastSessionData: ChargeSession?,
) {
    // when charging or when the summary is ready the session is considered active until
    // the user properly finalizes the session by seen the summary and closing it.
    val isSessionActive = isSessionRunning || isSessionSummaryReady
}
