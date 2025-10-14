package de.elvah.charge.features.sites.domain.usecase

import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.CampaignId
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.model.filters.OrganisationId
import de.elvah.charge.features.sites.domain.model.filters.SiteFilter
import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import de.elvah.charge.public_api.banner.EvseId


internal class UpdateFilters(
    private val filtersRepository: FiltersRepository,
) {
    suspend operator fun invoke(
        evseIds: List<EvseId> = emptyList(),
        boundingBox: BoundingBox? = null,
        campaignId: String? = null,
        organisationId: String? = null,
        offerType: OfferType? = null
    ) {
        filtersRepository.updateFilters(
            SiteFilter(
                boundingBox = boundingBox,
                campaignId = campaignId?.let { CampaignId(it) },
                organisationId = organisationId?.let { OrganisationId(it) },
                offerType = offerType
            )
        )
    }

    suspend operator fun invoke(
        evseIds: List<EvseId>,
        offerType: OfferType? = null
    ) {
        filtersRepository.updateFilters(
            SiteFilter(
                evseIds = evseIds,
                offerType = offerType
            )
        )
    }

    suspend operator fun invoke(minLat: Double, minLng: Double, maxLat: Double, maxLng: Double) {
        filtersRepository.updateBoundingBox(BoundingBox(minLat, minLng, maxLat, maxLng))
    }

    suspend operator fun invoke(boundingBox: BoundingBox) {
        filtersRepository.updateBoundingBox(boundingBox)
    }

    suspend operator fun invoke(campaignId: CampaignId) {
        filtersRepository.updateCampaignId(campaignId)
    }

    suspend operator fun invoke(organisationId: OrganisationId) {
        filtersRepository.updateOrganisationId(organisationId)
    }

    suspend operator fun invoke(offerType: OfferType) {
        filtersRepository.updateOfferType(offerType)
    }
}
