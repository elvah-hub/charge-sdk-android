package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OriginalPriceDto(

	@param:Json(name="energyPricePerKWh")
	val energyPricePerKWh: Any,

	@param:Json(name="baseFee")
	val baseFee: Any,

	@param:Json(name="currency")
	val currency: String,

	@param:Json(name="blockingFee")
	val blockingFee: Any
)
