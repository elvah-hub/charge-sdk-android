package de.elvah.charge.features.sites.data

import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.CampaignId
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.model.filters.OrganisationId
import de.elvah.charge.features.sites.domain.model.filters.SiteFilter
import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


internal class DefaultFiltersRepository() : FiltersRepository {

    private val _filters: MutableSharedFlow<SiteFilter> = MutableSharedFlow(replay = 1)
    private var currentFilter = SiteFilter()

    override val filters: Flow<SiteFilter> = _filters.asSharedFlow()

    override suspend fun updateFilters(filters: SiteFilter) {
        currentFilter = filters
        _filters.emit(currentFilter)
    }

    override suspend fun updateBoundingBox(
        boundingBox: BoundingBox
    ) {
        val updatedFilter = currentFilter.copy(
            boundingBox = boundingBox
        )
        updateFilters(updatedFilter)
    }

    override suspend fun updateCampaignId(campaignId: CampaignId) {
        val updatedFilter = currentFilter.copy(
            campaignId = campaignId
        )
        updateFilters(updatedFilter)
    }

    override suspend fun updateOrganisationId(organisationId: OrganisationId) {
        val updatedFilter = currentFilter.copy(
            organisationId = organisationId
        )
        updateFilters(updatedFilter)
    }

    override suspend fun updateOfferType(offerType: OfferType) {
        val updatedFilter = currentFilter.copy(
            offerType = offerType
        )
        updateFilters(updatedFilter)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun clearFilters() {
        _filters.resetReplayCache()
    }
}
