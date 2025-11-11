package de.elvah.charge.public_api.model

public data class ChargingSessionState(
    val isSessionRunning: Boolean,
    val isSessionSummaryReady: Boolean,
    val lastSessionData: ChargeSession?,
) {
    // when charging or when the summary is ready the session is considered active until
    // the user properly finalizes the session by seen the summary and closing it.
    val isSessionActive: Boolean = isSessionRunning || isSessionSummaryReady
}
