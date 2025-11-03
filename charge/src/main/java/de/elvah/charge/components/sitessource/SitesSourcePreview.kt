package de.elvah.charge.components.sitessource

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow
import de.elvah.charge.public_api.model.EvseId
import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

internal class SitesSourcePreview : SitesSource, InternalSitesSource {

    override val instanceId = UUID.randomUUID().toString()

    override val config: Config
        get() = Config(
            apiKey = "evpk_test_preview",
            darkTheme = null,
            environment = Environment.Simulator(SimulatorFlow.Default),
        )

    override val activeSession: StateFlow<ChargingSession?>
        get() = MutableStateFlow(null)

    override val siteIds: StateFlow<List<String>>
        get() = MutableStateFlow(emptyList())

    override val isIdle: Boolean
        get() = true

    override val sites: StateFlow<List<ChargeSite>>
        get() = MutableStateFlow(emptyList())

    override suspend fun sitesAt(
        boundingBox: BoundingBox,
        offerType: OfferType?
    ) {
        setMockData()
    }

    override suspend fun sitesAt(
        latitude: Double,
        longitude: Double,
        radius: Double,
        offerType: OfferType?
    ) {
        setMockData()
    }

    override suspend fun sitesAt(
        evseIds: List<EvseId>,
        offerType: OfferType?
    ) {
        setMockData()
    }

    override suspend fun clearSite() {
        // TODO: clear mock data
    }

    private fun setMockData() {
        // TODO: emit mock data to sites
    }

    override fun getSite(siteId: String): Either<Throwable, ChargeSite> {
        return Either.Left(Throwable("Site not found"))
    }

    override suspend fun getSiteScheduledPricing(siteId: String): Either<Throwable, ScheduledPricing> {
        return Either.Left(Throwable("Site not found"))
    }

    override suspend fun updateSiteAvailability(siteId: String): List<ChargeSite.ChargePoint>? {
        return null
    }
}
