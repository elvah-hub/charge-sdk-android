package de.elvah.charge.features.adhoc_charging.data.mapper

import de.elvah.charge.features.adhoc_charging.data.remote.model.response.ActiveChargeSessionsDto
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus

internal fun ActiveChargeSessionsDto.toDomain() = ChargingSession(
    evseId = data.evseId,
    status = data.status,
    consumption = data.consumption ?: 0.0,
    duration = data.duration ?: 0,
    status1 = parseStatus(data.status)

)

private fun parseStatus(status: String): SessionStatus = when (status) {
    "START_REQUESTED" -> SessionStatus.START_REQUESTED
    "STARTED" -> SessionStatus.STARTED
    "START_REJECTED" -> SessionStatus.START_REJECTED
    "CHARGING" -> SessionStatus.CHARGING
    "STOPPED" -> SessionStatus.STOPPED
    "STOP_REQUESTED" -> SessionStatus.STOP_REQUESTED
    "STOP_REJECTED" -> SessionStatus.STOP_REJECTED
    else -> SessionStatus.START_REQUESTED
}
