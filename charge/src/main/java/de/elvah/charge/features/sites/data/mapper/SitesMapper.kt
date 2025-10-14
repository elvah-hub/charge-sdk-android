package de.elvah.charge.features.sites.data.mapper

import de.elvah.charge.features.sites.data.remote.model.response.site.SitesDto
import de.elvah.charge.features.sites.domain.model.ChargeSite

internal fun SitesDto.toDomain(): ChargeSite {
    return ChargeSite(
        address = address.toDomain(),
        evses = evses.map { it.toDomain() },
        location = location,
        id = id,
        operatorName = operatorName,
        prevalentPowerType = prevalentPowerType
    )
}
