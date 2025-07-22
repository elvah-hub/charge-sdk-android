package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SignedOfferDto(

	@param:Json(name = "originalPrice")
	val originalPrice: PriceDto,

	@param:Json(name="price")
	val price: PriceDto,

	@param:Json(name="campaignEndsAt")
	val campaignEndsAt: String,

	@param:Json(name="signedOffer")
	val signedOffer: String,

	@param:Json(name="type")
	val type: String,

	@param:Json(name="expiresAt")
	val expiresAt: String
)
