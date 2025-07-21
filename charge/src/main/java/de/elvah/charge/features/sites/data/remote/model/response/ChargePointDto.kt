package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json

data class ChargePointDto<T>(

	@param:Json(name="evseId")
	val evseId: String,

	@param:Json(name="offer")
	val offer: T,

	@param:Json(name="powerSpecification")
	val powerSpecification: PowerSpecificationDto,

	@param:Json(name="normalizedEvseId")
	val normalizedEvseId: String
)
