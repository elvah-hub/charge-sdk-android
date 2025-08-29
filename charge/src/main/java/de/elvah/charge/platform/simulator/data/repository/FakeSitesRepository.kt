package de.elvah.charge.platform.simulator.data.repository

import arrow.core.Either
import arrow.core.right
import de.elvah.charge.entrypoints.banner.EvseId
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.features.sites.ui.utils.MockData
import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow

internal class FakeSitesRepository(simulatorFlow: SimulatorFlow) : SitesRepository {

    private var chargeSites: List<ChargeSite> = emptyList()

    override fun getChargeSite(siteId: String): Either<Throwable, ChargeSite> {
        return chargeSites.first { it.id == siteId }.right()
    }

    override fun updateChargeSite(site: ChargeSite) {
        if (chargeSites.none { it.id == site.id }) {
            chargeSites = chargeSites + site
        } else {
            chargeSites = chargeSites.map {
                if (it.id == site.id) {
                    site
                } else {
                    it
                }
            }
        }
    }

    override suspend fun getChargeSites(
        boundingBox: BoundingBox?,
        campaignId: String?,
        organisationId: String?,
        offerType: OfferType?,
        evseIds: List<EvseId>?
    ): Either<Throwable, List<ChargeSite>> {
        return MockData.chargeSites.right().also {
            it.getOrNull()?.let {
                chargeSites = it
            }
        }
    }

    override suspend fun getSignedOffer(
        siteId: String,
        evseId: String
    ): Either<Exception, ChargeSite> {
        return MockData.chargeSites.first().right()
    }

    companion object {
        const val MIN_LAT_KEY = "minLat"
        const val MAX_LAT_KEY = "maxLat"
        const val MIN_LNG_KEY = "minLng"
        const val MAX_LNG_KEY = "maxLng"
        const val CAMPAIGN_ID_KEY = "campaignId"
        const val ORGANISATION_ID_KEY = "organisationId"
        const val OFFER_TYPE_KEY = "offerType"
    }
}
