package de.elvah.charge.features.sites.data

import arrow.core.Either
import arrow.core.right
import de.elvah.charge.entrypoints.banner.EvseId
import de.elvah.charge.features.sites.data.mapper.toDomain
import de.elvah.charge.features.sites.data.mapper.toSite
import de.elvah.charge.features.sites.data.remote.SitesApi
import de.elvah.charge.features.sites.data.remote.model.request.SignedOfferRequest
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.platform.core.arrow.extensions.toEither


internal class DefaultSitesRepository(
    private val sitesApi: SitesApi,
) : SitesRepository {

    private var chargeSites: List<ChargeSite> = emptyList()

    override fun getChargeSite(siteId: String): Either<Throwable, ChargeSite> {
        return chargeSites.firstOrNull { it.id == siteId }?.right() ?: Either.Left(
            Exception("Site not found")
        )
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
        return runCatching {
            sitesApi.getSites(
                evseIds?.map { it.value },
                parseFilters(
                    boundingBox,
                    campaignId,
                    organisationId,
                    offerType,
                )
            ).data.map {
                it.toSite()
            }.also {
                chargeSites = it
            }
        }.toEither()
    }

    override suspend fun getSignedOffer(
        siteId: String,
        evseId: String
    ): Either<Throwable, ChargeSite> {
        return runCatching {
            sitesApi.getSignedOffer(
                siteId = siteId,
                signedOfferRequest = SignedOfferRequest(
                    evseIds = listOf(evseId)
                )
            ).data.toDomain()
        }.toEither()
    }

    private fun parseFilters(
        boundingBox: BoundingBox? = null,
        campaignId: String? = null,
        organisationId: String? = null,
        offerType: OfferType? = null,
    ): Map<String, String> {
        return buildMap {
            boundingBox?.let {
                put(MIN_LAT_KEY, it.minLat.toString())
                put(MAX_LAT_KEY, it.maxLat.toString())
                put(MIN_LNG_KEY, it.minLng.toString())
                put(MAX_LNG_KEY, it.maxLng.toString())
            }
            campaignId?.let {
                put(CAMPAIGN_ID_KEY, it)
            }
            organisationId?.let {
                put(ORGANISATION_ID_KEY, it)
            }
            offerType?.let {
                put(OFFER_TYPE_KEY, it.name)
            }
        }
    }

    companion object {
        const val MIN_LAT_KEY = "minLat"
        const val MAX_LAT_KEY = "maxLat"
        const val MIN_LNG_KEY = "minLng"
        const val MAX_LNG_KEY = "maxLng"
        const val CAMPAIGN_ID_KEY = "campaignId"
        const val ORGANISATION_ID_KEY = "organisationId"
        const val OFFER_TYPE_KEY = "offerType"
        const val EVSE_IDS_KEY = "evseIds"
    }
}
