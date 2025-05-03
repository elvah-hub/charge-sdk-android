package de.elvah.charge.features.adhoc_charging.data.mapper

import de.elvah.charge.features.adhoc_charging.data.remote.model.response.ActiveChargeSessionsDto
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession

internal fun ActiveChargeSessionsDto.toDomain() = ChargingSession(
    evseId = data.evseId,
    status = data.status,
    consumption = data.consumption ?: 0.0,
    duration = data.duration ?: 0,
)