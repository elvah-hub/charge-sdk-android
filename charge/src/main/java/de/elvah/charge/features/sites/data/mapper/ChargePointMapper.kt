package de.elvah.charge.features.sites.data.mapper

import de.elvah.charge.features.sites.data.remote.model.response.site.EvseDto
import de.elvah.charge.features.sites.domain.model.ChargeSite

internal fun EvseDto.toDomain(): ChargeSite.ChargePoint = ChargeSite.ChargePoint(
    evseId = evseId,
    offer = offer.toDomain(),
    powerSpecification = powerSpecification?.toDomain(),
    availability = availability.toDomain(),
    normalizedEvseId = normalizedEvseId
)
