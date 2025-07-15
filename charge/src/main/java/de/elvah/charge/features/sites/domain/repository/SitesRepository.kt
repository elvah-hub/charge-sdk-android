package de.elvah.charge.features.sites.domain.repository

import arrow.core.Either
import de.elvah.charge.features.deals.domain.model.Deal
import de.elvah.charge.features.sites.domain.model.ChargeSite

internal interface SitesRepository {

    fun getChargeSite(siteId: String): ChargeSite

    suspend fun getChargeSites(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double,
    ): Either<Exception, List<ChargeSite>>
}
