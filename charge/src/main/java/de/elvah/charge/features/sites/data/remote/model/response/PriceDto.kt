package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PriceDto(

	@param:Json(name="energyPricePerKWh")
	val energyPricePerKWh: Double,

	@param:Json(name="baseFee")
	val baseFee: Int?,

	@param:Json(name="currency")
	val currency: String,

	@param:Json(name="blockingFee")
	val blockingFee: BlockingFeeDto?
)
