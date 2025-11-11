package de.elvah.charge.features.sites.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.public_api.model.EvseId

internal class GetSites(
    private val sitesRepository: SitesRepository,
) {

    suspend operator fun invoke(params: Params): Either<Throwable, List<ChargeSite>> {
        return sitesRepository.getChargeSites(
            boundingBox = params.boundingBox,
            campaignId = params.campaignId,
            organisationId = params.organisationId,
            offerType = params.offerType,
            evseIds = params.evseIds
        )
    }

    internal class Params(
        val boundingBox: BoundingBox? = null,
        val campaignId: String? = null,
        val organisationId: String? = null,
        val offerType: OfferType? = null,
        val evseIds: List<EvseId>? = null,
    )
}
