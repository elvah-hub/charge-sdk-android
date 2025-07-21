package de.elvah.charge.features.sites.data

import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.model.filters.SiteFilter
import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import de.elvah.charge.features.sites.domain.usecase.CampaignId
import de.elvah.charge.features.sites.domain.usecase.OrganisationId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


internal class DefaultFiltersRepository(
) : FiltersRepository {


    private val _filters: MutableSharedFlow<SiteFilter> = MutableSharedFlow(replay = 1)
    private var currentFilter = SiteFilter()

    override val filters: Flow<SiteFilter> = _filters.asSharedFlow()

    override suspend fun updateFilters(filters: SiteFilter) {
        _filters.emit(filters)
    }

    override suspend fun updateBoundingBox(
        boundingBox: BoundingBox
    ) {
        val updatedFilter = currentFilter.copy(
            boundingBox = boundingBox
        )
        updateFilters(updatedFilter).also {
            currentFilter = updatedFilter
        }
    }

    override suspend fun updateCampaignId(campaignId: CampaignId) {
        val updatedFilter = currentFilter.copy(
            campaignId = campaignId.value
        )
        updateFilters(updatedFilter).also {
            currentFilter = updatedFilter
        }
    }

    override suspend fun updateOrganisationId(organisationId: OrganisationId) {
        val updatedFilter = currentFilter.copy(
            organisationId = organisationId.value
        )
        updateFilters(updatedFilter).also {
            currentFilter = updatedFilter
        }
    }

    override suspend fun updateOfferType(offerType: OfferType) {
        val updatedFilter = currentFilter.copy(
            offerType = offerType
        )
        updateFilters(updatedFilter).also {
            currentFilter = updatedFilter
        }
    }
}
