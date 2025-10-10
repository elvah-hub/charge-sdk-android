package de.elvah.charge.components.sitessource

import arrow.core.Either
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import kotlinx.coroutines.flow.StateFlow

internal interface InternalSitesSource {

    val sites: StateFlow<List<ChargeSite>?>

    fun getSite(siteId: String): Either<Throwable, ChargeSite>

    suspend fun getSiteScheduledPricing(siteId: String): Either<Throwable, ScheduledPricing>

    suspend fun updateSiteAvailability(siteId: String): List<ChargeSite.ChargePoint>?
}
