package de.elvah.charge.features.sites.domain.model.filters

import de.elvah.charge.entrypoints.banner.EvseId

data class SiteFilter(
    val evseIds: List<EvseId> = emptyList(),
    var boundingBox: BoundingBox? = null,
    var campaignId: CampaignId? = null,
    var organisationId: OrganisationId? = null,
    var offerType: OfferType? = null
)
