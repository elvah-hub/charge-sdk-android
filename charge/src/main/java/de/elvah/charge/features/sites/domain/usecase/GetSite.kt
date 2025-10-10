package de.elvah.charge.features.sites.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.repository.SitesRepository

internal class GetSite(
    private val sitesRepository: SitesRepository,
) {

    operator fun invoke(siteId: String): Either<Throwable, ChargeSite> {
        return sitesRepository.getChargeSite(siteId)
    }
}
