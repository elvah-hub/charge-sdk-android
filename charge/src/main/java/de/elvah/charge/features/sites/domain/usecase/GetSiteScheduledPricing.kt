package de.elvah.charge.features.sites.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.domain.repository.SitesRepository

internal class GetSiteScheduledPricing(
    private val sitesRepository: SitesRepository,
) {

    suspend operator fun invoke(siteId: String): Either<Throwable, ScheduledPricing> {
        return sitesRepository.getSiteScheduledPricing(siteId)
    }
}
