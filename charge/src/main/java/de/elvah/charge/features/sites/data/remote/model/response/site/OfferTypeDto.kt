package de.elvah.charge.features.sites.data.remote.model.response.site

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.elvah.charge.features.sites.data.remote.model.response.PriceDto

internal sealed interface OfferTypeDto {

    @JsonClass(generateAdapter = true)
    data class OfferPreviewStandardDto(

        @param:Json(name = "expiresAt")
        val expiresAt: String,

        @param:Json(name = "price")
        val price: PriceDto,

        @param:Json(name = "type")
        val type: String,
    ) : OfferTypeDto

    @JsonClass(generateAdapter = true)
    data class OfferPreviewCampaignDto(

        @param:Json(name = "campaignEndsAt")
        val campaignEndsAt: String,

        @param:Json(name = "expiresAt")
        val expiresAt: String,

        @param:Json(name = "originalPrice")
        val originalPrice: PriceDto,

        @param:Json(name = "price")
        val price: PriceDto,

        @param:Json(name = "type")
        val type: String,
    ) : OfferTypeDto

    @JsonClass(generateAdapter = true)
    data class SignedOfferStandardDto(

        @param:Json(name = "expiresAt")
        val expiresAt: String,

        @param:Json(name = "price")
        val price: PriceDto,

        @param:Json(name = "signedOffer")
        val signedOffer: String,

        @param:Json(name = "type")
        val type: String,
    ) : OfferTypeDto

    @JsonClass(generateAdapter = true)
    data class SignedOfferCampaignDto(

        @param:Json(name = "campaignEndsAt")
        val campaignEndsAt: String,

        @param:Json(name = "expiresAt")
        val expiresAt: String,

        @param:Json(name = "originalPrice")
        val originalPrice: PriceDto,

        @param:Json(name = "price")
        val price: PriceDto,

        @param:Json(name = "signedOffer")
        val signedOffer: String,

        @param:Json(name = "type")
        val type: String,
    ) : OfferTypeDto
}
