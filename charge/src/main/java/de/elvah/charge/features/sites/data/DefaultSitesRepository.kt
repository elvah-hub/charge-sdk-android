package de.elvah.charge.features.sites.data

import arrow.core.Either
import de.elvah.charge.features.sites.data.mapper.toDomain
import de.elvah.charge.features.sites.data.remote.SitesApi
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.platform.core.arrow.extensions.toEither


internal class DefaultSitesRepository(
    private val sitesApi: SitesApi,
) : SitesRepository {

    private var chargeSites: List<ChargeSite> = emptyList()

    override fun getChargeSite(siteId: String): ChargeSite {
        return chargeSites.first { it.id == siteId }
    }

    override suspend fun getChargeSites(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double,
    ): Either<Exception, List<ChargeSite>> {
        return runCatching {
            sitesApi.getSites(
                minLat = minLat,
                maxLat = maxLat,
                minLng = minLng,
                maxLng = maxLng
            ).data.map { it.toDomain() }.also {
                chargeSites = it
            }
        }.toEither()
    }
}
