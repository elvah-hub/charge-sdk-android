package de.elvah.charge.features.sites.data.remote

import com.squareup.moshi.Json

data class SitesDto(

	@param:Json(name="data")
	val data: List<DataItemDto>,

	@param:Json(name="meta")
	val meta: MetaDto
)

data class BlockingFeeDto(

	@param:Json(name="pricePerMinute")
	val pricePerMinute: Int,

	@param:Json(name="startsAfterMinutes")
	val startsAfterMinutes: Int
)

data class PowerSpecificationDto(

	@param:Json(name="maxPowerInKW")
	val maxPowerInKW: Int,

	@param:Json(name="type")
	val type: String
)

data class OfferDto(

	@param:Json(name="price")
	val price: PriceDto,

	@param:Json(name="type")
	val type: String,

	@param:Json(name="expiresAt")
	val expiresAt: String
)

data class EvsesItemDto(

	@param:Json(name="evseId")
	val evseId: String,

	@param:Json(name="offer")
	val offer: OfferDto,

	@param:Json(name="powerSpecification")
	val powerSpecification: PowerSpecificationDto,

	@param:Json(name="normalizedEvseId")
	val normalizedEvseId: String
)

data class DataItemDto(

	@param:Json(name="address")
	val address: AddressDto,

	@param:Json(name="evses")
	val evses: List<EvsesItemDto>,

	@param:Json(name="location")
	val location: List<Int>,

	@param:Json(name="id")
	val id: String,

	@param:Json(name="operatorName")
	val operatorName: String,

	@param:Json(name="prevalentPowerType")
	val prevalentPowerType: String
)

data class PriceDto(

	@param:Json(name="energyPricePerKWh")
	val energyPricePerKWh: Int,

	@param:Json(name="baseFee")
	val baseFee: Int,

	@param:Json(name="currency")
	val currency: String,

	@param:Json(name="blockingFee")
	val blockingFee: BlockingFeeDto
)

data class MetaDto(
	val any: Any? = null
)

data class AddressDto(

	@param:Json(name="streetAddress")
	val streetAddress: List<String>,

	@param:Json(name="postalCode")
	val postalCode: String,

	@param:Json(name="locality")
	val locality: String
)
