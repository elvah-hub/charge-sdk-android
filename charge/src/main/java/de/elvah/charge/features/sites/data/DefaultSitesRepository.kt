package de.elvah.charge.features.sites.data

import arrow.core.Either
import arrow.core.right
import de.elvah.charge.features.sites.data.mapper.toDomain
import de.elvah.charge.features.sites.data.remote.SitesApi
import de.elvah.charge.features.sites.data.remote.model.request.SignedOfferRequest
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.platform.core.arrow.extensions.toEither
import de.elvah.charge.public_api.model.EvseId

internal class DefaultSitesRepository(
    private val sitesApi: SitesApi,
) : SitesRepository {

    private var chargeSites: List<ChargeSite> = emptyList()

    private var currentEvseIds: List<String>? = null
    private var currentFilter: Map<String, String> = mapOf()

    override fun getChargeSite(siteId: String): Either<Throwable, ChargeSite> {
        return chargeSites
            .find { site -> site.id == siteId }
            ?.right()
            ?: Either.Left(Exception("Site not found"))
    }

    override suspend fun getChargeSites(
        boundingBox: BoundingBox?,
        campaignId: String?,
        organisationId: String?,
        offerType: OfferType?,
        evseIds: List<EvseId>?
    ): Either<Throwable, List<ChargeSite>> {
        return runCatching {
            val requestEvseIds = evseIds?.map { it.value }

            val requestFilters = parseFilters(
                boundingBox,
                campaignId,
                organisationId,
                offerType,
            )

            when {
                // TODO: include timestamp check to allow requesting to api again after x time
                currentEvseIds == requestEvseIds && currentFilter == requestFilters -> chargeSites

                else -> sitesApi
                    .getSiteOffers(
                        evseIds = requestEvseIds,
                        filters = requestFilters,
                    )
                    .data
                    .map { it.toDomain() }
                    .also {
                        currentFilter = requestFilters
                        currentEvseIds = requestEvseIds
                        chargeSites = it
                    }
            }

        }.toEither()
    }

    override suspend fun getSignedOffer(
        siteId: String,
        evseId: String
    ): Either<Throwable, ChargeSite> {
        return runCatching {
            sitesApi.getSiteOffer(
                siteId = siteId,
                signedOfferRequest = SignedOfferRequest(
                    evseIds = listOf(evseId)
                )
            ).data.toDomain()
        }.toEither()
    }

    override suspend fun getSiteScheduledPricing(siteId: String): Either<Throwable, ScheduledPricing> {
        // TODO: cache information here, emit loading state if calling api.
        return sitesApi.getSiteScheduledPricing(siteId).map { it.data.toDomain() }
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

    override suspend fun updateChargePointAvailabilities(
        siteId: String,
    ): Either<Throwable, List<ChargeSite.ChargePoint>> {
        return runCatching {
            val site = chargeSites.firstOrNull { it.id == siteId }
                ?: return Either.Left(Exception("Site not found"))

            val response = sitesApi.getChargePointAvailabilities(site.id)
                .data

            val updateSites = chargeSites.toMutableList()

            val updatedEvses = site.evses.map { cp ->
                val newAvailability = response.evses.find { it.evseId == cp.evseId }
                    ?.availability?.toDomain()
                    ?: cp.availability

                cp.copy(
                    availability = newAvailability,
                )
            }

            val updateSite = updateSites.find { s -> s.id == siteId }
                ?.copy(
                    evses = updatedEvses,
                )

            updateSite?.let {
                updateSites[updateSites.indexOf(site)] = it
            }

            chargeSites = updateSites

            updateSite?.evses ?: listOf()
        }.toEither()
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
