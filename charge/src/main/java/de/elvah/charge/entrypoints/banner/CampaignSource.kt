package de.elvah.charge.entrypoints.banner

import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceAtMost
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.usecase.UpdateFilters
import org.koin.java.KoinJavaComponent

class CampaignSource() {

    private val updateFilters: UpdateFilters by KoinJavaComponent.inject(UpdateFilters::class.java)

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

    suspend fun sitesAt(
        evseIds: List<EvseId>,
        offerType: OfferType? = null
    ) {
        updateFilters(evseIds = evseIds, offerType = offerType)
    }
}


@JvmInline
value class EvseId(val value: String)
