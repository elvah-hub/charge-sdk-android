package de.elvah.charge.features.sites.domain.repository

import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.CampaignId
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.model.filters.OrganisationId
import de.elvah.charge.features.sites.domain.model.filters.SiteFilter
import kotlinx.coroutines.flow.Flow

internal interface FiltersRepository {

    val filters: Flow<SiteFilter>

    suspend fun updateFilters(filters: SiteFilter)
    suspend fun updateBoundingBox(boundingBox: BoundingBox)
    suspend fun updateCampaignId(campaignId: CampaignId)
    suspend fun updateOrganisationId(organisationId: OrganisationId)
    suspend fun updateOfferType(offerType: OfferType)
}
