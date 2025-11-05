package de.elvah.charge.public_api.mapper

import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.public_api.model.ChargingSession as PublicChargingSession
import de.elvah.charge.public_api.model.SessionStatus as PublicSessionStatus

internal fun ChargeSession.toPublic(): PublicChargingSession {
    return PublicChargingSession(
        evseId = evseId,
        status = status.toPublic(),
        consumption = consumption,
        duration = duration
    )
}

internal fun SessionStatus.toPublic(): PublicSessionStatus {
    return when (this) {
        SessionStatus.START_REQUESTED -> PublicSessionStatus.START_REQUESTED
        SessionStatus.STARTED -> PublicSessionStatus.STARTED
        SessionStatus.START_REJECTED -> PublicSessionStatus.START_REJECTED
        SessionStatus.CHARGING -> PublicSessionStatus.CHARGING
        SessionStatus.STOPPED -> PublicSessionStatus.STOPPED
        SessionStatus.STOP_REQUESTED -> PublicSessionStatus.STOP_REQUESTED
        SessionStatus.STOP_REJECTED -> PublicSessionStatus.STOP_REJECTED
    }
}
