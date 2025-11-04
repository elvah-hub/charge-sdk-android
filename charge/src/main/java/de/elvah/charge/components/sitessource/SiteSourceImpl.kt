package de.elvah.charge.components.sitessource

import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceAtMost
import arrow.core.Either
import arrow.core.getOrElse
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.usecase.ClearFilters
import de.elvah.charge.features.sites.domain.usecase.GetFilters
import de.elvah.charge.features.sites.domain.usecase.GetSite
import de.elvah.charge.features.sites.domain.usecase.GetSiteScheduledPricing
import de.elvah.charge.features.sites.domain.usecase.GetSites
import de.elvah.charge.features.sites.domain.usecase.GetSites.Params
import de.elvah.charge.features.sites.domain.usecase.UpdateFilters
import de.elvah.charge.features.sites.domain.usecase.UpdateSiteAvailability
import de.elvah.charge.platform.config.Config
import de.elvah.charge.public_api.model.EvseId
import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.UUID

internal class SitesSourceImpl(
    private val configuration: Config,
    getFilters: GetFilters,
    private val updateFilters: UpdateFilters,
    private val clearFilters: ClearFilters,
    private val getSite: GetSite,
    private val getSites: GetSites,
    private val getSiteScheduledPricing: GetSiteScheduledPricing,
    private val updateSiteAvailability: UpdateSiteAvailability,
) : SitesSource, InternalSitesSource {

    private var parentJob = SupervisorJob()
    private var coroutineScope = CoroutineScope(parentJob + Dispatchers.IO)

    override val instanceId = UUID.randomUUID().toString()

    override val config: Config
        get() = configuration

    private var _isIdle: Boolean = true
    override val isIdle: Boolean
        get() = _isIdle

    override val siteIds: StateFlow<List<String>>
        get() = sites
            .map { sites -> sites?.mapSiteId() ?: emptyList() }
            .stateIn(
                scope = coroutineScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = sites.value?.mapSiteId() ?: emptyList(),
            )

    override val sites: StateFlow<List<ChargeSite>?> = getFilters()
        .map { filters ->
            val params = Params(
                boundingBox = filters.boundingBox,
                campaignId = filters.campaignId?.value,
                organisationId = filters.organisationId?.value,
                offerType = filters.offerType,
                evseIds = filters.evseIds,
            )

            getSites(params)
                .getOrElse { emptyList() }
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    private fun List<ChargeSite>.mapSiteId() = map { site -> site.id }

    override suspend fun sitesAt(
        boundingBox: BoundingBox,
        offerType: OfferType?,
    ) {
        sitesAtCall {
            updateFilters(boundingBox = boundingBox, offerType = offerType)
        }
    }

    override suspend fun sitesAt(
        latitude: Double,
        longitude: Double,
        radius: Double,
        offerType: OfferType?,
    ) {
        sitesAtCall {
            val radioInDegrees = radius / 11
            updateFilters(
                boundingBox = BoundingBox(
                    minLat = (latitude - radioInDegrees).fastCoerceAtLeast(-90.0),
                    minLng = (longitude - radioInDegrees).fastCoerceAtLeast(-180.0),
                    maxLat = (latitude + radioInDegrees).fastCoerceAtMost(90.0),
                    maxLng = (longitude + radioInDegrees).fastCoerceAtMost(180.0)
                ),
                offerType = offerType
            )
        }
    }

    override suspend fun sitesAt(
        evseIds: List<EvseId>,
        offerType: OfferType?,
    ) {
        sitesAtCall {
            updateFilters(evseIds = evseIds, offerType = offerType)
        }
    }

    override suspend fun clearSite() {
        clearFilters()
    }

    override fun getSite(siteId: String): Either<Throwable, ChargeSite> {
        return getSite.invoke(siteId)
    }

    override suspend fun getSiteScheduledPricing(siteId: String): Either<Throwable, ScheduledPricing> {
        return getSiteScheduledPricing
            .invoke(siteId)
    }

    override suspend fun updateSiteAvailability(siteId: String): List<ChargeSite.ChargePoint>? {
        return updateSiteAvailability
            .invoke(siteId)
            .getOrNull()
    }

    private suspend fun sitesAtCall(
        action: suspend () -> Unit,
    ) {
        if (_isIdle) _isIdle = false
        action()
    }
}
