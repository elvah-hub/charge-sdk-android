package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json

data class PowerSpecificationDto(

	@param:Json(name="maxPowerInKW")
	val maxPowerInKW: Int,

	@param:Json(name="type")
	val type: String
)
