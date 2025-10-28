package de.elvah.charge.public_api.sitessource

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import de.elvah.charge.components.sitessource.SitesSourcePreview
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.manager.di.injectSitesSource
import de.elvah.charge.platform.config.Config
import de.elvah.charge.public_api.model.EvseId
import kotlinx.coroutines.flow.StateFlow

public interface SitesSource {

    public val instanceId: String

    public val config: Config

    // TODO: is necessary here? charge service is a single instance, use chargeService directly
    public val activeSession: StateFlow<ChargingSession?>

    public val siteIds: StateFlow<List<String>>

    public suspend fun sitesAt(
        boundingBox: BoundingBox,
        offerType: OfferType? = null,
    )

    public suspend fun sitesAt(
        latitude: Double,
        longitude: Double,
        radius: Double,
        offerType: OfferType? = null,
    )

    public suspend fun sitesAt(
        evseIds: List<EvseId>,
        offerType: OfferType? = null,
    )

    public suspend fun clearSite()

    public companion object {

        private const val SAVER_CLIENT_ID = "clientId"
        private const val SAVER_INSTANCE_ID = "instanceId"

        internal fun getSourceSaver(
            clientId: String,
            inspectionMode: Boolean,
        ): Saver<SitesSource, Any> = mapSaver(
            save = { sitesSource: SitesSource ->
                mapOf(
                    SAVER_CLIENT_ID to clientId,
                    SAVER_INSTANCE_ID to sitesSource.instanceId,
                )
            },
            restore = { map ->
                when {
                    inspectionMode -> SitesSourcePreview()
                    else -> injectSitesSource(
                        clientId = map[SAVER_CLIENT_ID] as String,
                        instanceId = map[SAVER_INSTANCE_ID] as? String,
                    )
                }
            }
        )
    }
}
