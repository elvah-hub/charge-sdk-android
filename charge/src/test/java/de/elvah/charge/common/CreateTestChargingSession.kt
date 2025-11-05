package de.elvah.charge.common

import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.platform.simulator.data.repository.SessionStatus

internal fun createTestChargingSession(
    status: SessionStatus = SessionStatus.CHARGING,
    consumption: Double = 15.5
) = ChargeSession(
    evseId = "DE*KDL*E0000040",
    consumption = consumption,
    duration = 120,
    status = status,
)
