package de.elvah.charge.features.sites.data.remote.model.request

import com.squareup.moshi.Json
import de.elvah.charge.features.sites.domain.model.filters.OfferType

internal class SignedOfferRequest(
    @param:Json(name = "evseIds")
    val evseIds: List<String>,
    @param:Json(name = "offerType")
    val offerType: OfferType? = null
)
