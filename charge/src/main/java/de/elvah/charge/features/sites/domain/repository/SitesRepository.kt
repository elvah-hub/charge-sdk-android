package de.elvah.charge.features.sites.domain.repository

import arrow.core.Either
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType

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
