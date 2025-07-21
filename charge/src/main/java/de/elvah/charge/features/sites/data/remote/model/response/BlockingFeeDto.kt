package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json

data class BlockingFeeDto(

	@param:Json(name="pricePerMinute")
	val pricePerMinute: Int,

	@param:Json(name="startsAfterMinutes")
	val startsAfterMinutes: Int
)
