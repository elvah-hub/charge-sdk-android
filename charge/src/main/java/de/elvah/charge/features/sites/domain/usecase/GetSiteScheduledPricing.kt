package de.elvah.charge.features.sites.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.domain.repository.SitesRepository

internal class GetSiteScheduledPricing(private val sitesRepository: SitesRepository) {
    suspend operator fun invoke(params: Params): Either<Throwable, ScheduledPricing> {
        return either {
            val scheduledPricing = sitesRepository.getSiteScheduledPricing(params.siteId)
            scheduledPricing.bind()
        }
    }

    internal class Params(
        val siteId: String
    )
}