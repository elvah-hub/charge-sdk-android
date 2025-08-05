package de.elvah.charge.entrypoints.banner

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
        evseIds: List<EvseId>,
        offerType: OfferType? = null
    ) {
        updateFilters(evseIds = evseIds, offerType = offerType)
    }
}


@JvmInline
value class EvseId(val value: String)
