package de.elvah.charge.features.sites.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.deals.domain.model.Deal
import de.elvah.charge.features.sites.data.DefaultSitesRepository
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType


internal class GetSite(
    private val getSites: GetSites,
) {
    suspend operator fun invoke(params: Params): Either<Exception, ChargeSite> {
        return getSites(
            with(params) {
                GetSites.Params(
                    boundingBox = boundingBox,
                    campaignId = campaignId,
                    organisationId = organisationId,
                    offerType = offerType
                )
            }
        ).flatMap {
            if (it.isNotEmpty()) {
                it.first().right()
            } else {
                Exception("No deals found").left()
            }
        }
    }

    internal class Params(
        val boundingBox: BoundingBox? = null,
        val campaignId: String? = null,
        val organisationId: String? = null,
        val offerType: OfferType? = null
    )
}
