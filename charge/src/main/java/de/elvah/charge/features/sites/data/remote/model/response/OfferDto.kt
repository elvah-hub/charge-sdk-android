package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json

internal data class OfferDto(

    @param:Json(name = "price")
    val price: PriceDto,

    @param:Json(name = "originalPrice")
    val originalPrice: PriceDto?,

    @param:Json(name = "type")
    val type: String,

    @param:Json(name = "expiresAt")
    val expiresAt: String,

    @param:Json(name = "campaignEndsAt")
    val campaignEndsAt: String?
)
