package de.elvah.charge.public_api.banner

import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.compose.ui.util.fastCoerceAtMost
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.usecase.ClearFilters
import de.elvah.charge.features.sites.domain.usecase.UpdateFilters
import org.koin.java.KoinJavaComponent

public class ChargeBannerSource() {

    private val updateFilters: UpdateFilters by KoinJavaComponent.inject(UpdateFilters::class.java)
    private val clearFilters: ClearFilters by KoinJavaComponent.inject(ClearFilters::class.java)

    public suspend fun sitesAt(
        boundingBox: BoundingBox,
        offerType: OfferType? = null
    ) {
        updateFilters(boundingBox = boundingBox, offerType = offerType)
    }

    public suspend fun sitesAt(
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

    public suspend fun sitesAt(
        evseIds: List<EvseId>,
        offerType: OfferType? = null
    ) {
        updateFilters(evseIds = evseIds, offerType = offerType)
    }

    public suspend fun resetFilters() {
        clearFilters()
    }
}


@JvmInline
public value class EvseId(public val value: String)
