package de.elvah.charge.features.sites.data.remote

import com.squareup.moshi.Json

data class SitesDto(

	@Json(name="data")
	val data: List<DataItemDto>,

	@Json(name="meta")
	val meta: MetaDto
)

data class BlockingFeeDto(

	@Json(name="pricePerMinute")
	val pricePerMinute: Int,

	@Json(name="startsAfterMinutes")
	val startsAfterMinutes: Int
)

data class PowerSpecificationDto(

	@Json(name="maxPowerInKW")
	val maxPowerInKW: Int,

	@Json(name="type")
	val type: String
)

data class OfferDto(

	@Json(name="price")
	val price: PriceDto,

	@Json(name="type")
	val type: String,

	@Json(name="expiresAt")
	val expiresAt: String
)

data class EvsesItemDto(

	@Json(name="evseId")
	val evseId: String,

	@Json(name="offer")
	val offer: OfferDto,

	@Json(name="powerSpecification")
	val powerSpecification: PowerSpecificationDto,

	@Json(name="normalizedEvseId")
	val normalizedEvseId: String
)

data class DataItemDto(

	@Json(name="address")
	val address: AddressDto,

	@Json(name="evses")
	val evses: List<EvsesItemDto>,

	@Json(name="location")
	val location: List<Int>,

	@Json(name="id")
	val id: String,

	@Json(name="operatorName")
	val operatorName: String,

	@Json(name="prevalentPowerType")
	val prevalentPowerType: String
)

data class PriceDto(

	@Json(name="energyPricePerKWh")
	val energyPricePerKWh: Int,

	@Json(name="baseFee")
	val baseFee: Int,

	@Json(name="currency")
	val currency: String,

	@Json(name="blockingFee")
	val blockingFee: BlockingFeeDto
)

data class MetaDto(
	val any: Any? = null
)

data class AddressDto(

	@Json(name="streetAddress")
	val streetAddress: List<String>,

	@Json(name="postalCode")
	val postalCode: String,

	@Json(name="locality")
	val locality: String
)
