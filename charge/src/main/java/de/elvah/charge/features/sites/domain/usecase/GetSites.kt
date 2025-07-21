package de.elvah.charge.features.sites.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import de.elvah.charge.features.deals.domain.model.Deal
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.repository.SitesRepository


internal class GetSites(private val sitesRepository: SitesRepository) {
    suspend operator fun invoke(params: Params): Either<Exception, List<ChargeSite>> {
        return either {
            val sites = sitesRepository.getChargeSites(
                boundingBox = params.boundingBox,
                campaignId = params.campaignId,
                organisationId = params.organisationId,
                offerType = params.offerType
            )

            sites.bind()
        }
    }

    internal class Params(
        val boundingBox: BoundingBox? = null,
        val campaignId: String? = null,
        val organisationId: String? = null,
        val offerType: OfferType? = null
    )
}
