package de.elvah.charge.features.sites.domain.repository

import arrow.core.Either
import de.elvah.charge.entrypoints.banner.EvseId
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType

internal interface SitesRepository {

    fun getChargeSite(siteId: String): Either<Throwable, ChargeSite>

    fun updateChargeSite(site: ChargeSite)

    suspend fun getChargeSites(
        boundingBox: BoundingBox? = null,
        campaignId: String? = null,
        organisationId: String? = null,
        offerType: OfferType? = null,
        evseIds: List<EvseId>? = null
    ): Either<Throwable, List<ChargeSite>>

    suspend fun getSignedOffer(siteId: String, evseId: String): Either<Throwable, ChargeSite>

    suspend fun getSiteScheduledPricing(siteId: String): Either<Throwable, ScheduledPricing>
}
