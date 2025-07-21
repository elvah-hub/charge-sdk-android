package de.elvah.charge.features.sites.domain.repository

import arrow.core.Either
import de.elvah.charge.entrypoints.banner.EvseId
import de.elvah.charge.features.deals.domain.model.Deal
import de.elvah.charge.features.deals.ui.model.SignedChargePointUI
import de.elvah.charge.features.sites.data.DefaultSitesRepository
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox

internal interface SitesRepository {

    fun getChargeSite(siteId: String): ChargeSite

    suspend fun getChargeSites(
        boundingBox: BoundingBox? = null,
        campaignId: String? = null,
        organisationId: String? = null,
        offerType: OfferType? = null
    ): Either<Exception, List<ChargeSite>>

    suspend fun getSignedOffer(siteId: String, evseId: String): Either<Exception, ChargeSite>

    //suspend fun getSignedOffer(siteId: String, evseId: EvseId): Either<Exception, SignedChargePoint>
}
