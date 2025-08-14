package de.elvah.charge.entrypoints.banner

import androidx.compose.ui.util.fastCoerceAtLeast
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.usecase.ClearFilters
import de.elvah.charge.features.sites.domain.usecase.UpdateFilters
import org.koin.java.KoinJavaComponent

class CampaignSource() {

    private val updateFilters: UpdateFilters by KoinJavaComponent.inject(UpdateFilters::class.java)
    private val clearFilters: ClearFilters by KoinJavaComponent.inject(ClearFilters::class.java)

    suspend fun sitesAt(
        boundingBox: BoundingBox,
        offerType: OfferType? = null
    ) {
        updateFilters(boundingBox = boundingBox, offerType = offerType)
    }

    suspend fun sitesAt(
        latitude: Double,
        longitude: Double,
        radius: Double,
        offerType: OfferType? = null
    ) {
        updateFilters(
            boundingBox = BoundingBox(
                minLat = (latitude - radius).fastCoerceAtLeast(-90.0),
                minLng = longitude - radius,
                maxLat = latitude + radius,
                maxLng = longitude + radius
            ),
            offerType = offerType
        )
    }

    suspend fun sitesAt(
        evseIds: List<EvseId>,
        offerType: OfferType? = null
    ) {
        updateFilters(evseIds = evseIds, offerType = offerType)
    }

    suspend fun resetFilters() {
        clearFilters()
    }
}


@JvmInline
value class EvseId(val value: String)
