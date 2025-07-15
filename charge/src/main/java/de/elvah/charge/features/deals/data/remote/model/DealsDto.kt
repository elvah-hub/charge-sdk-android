package de.elvah.charge.features.deals.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class DealsDto(

    @param:Json(name = "data")
    val data: List<CPOItem>,

    @param:Json(name = "meta")
    val meta: Meta?,
)

@JsonClass(generateAdapter = true)
internal class CPOItem(

    @param:Json(name = "deals")
    val deals: List<DealsItem>,

    @param:Json(name = "location")
    val location: List<Double>,

    @param:Json(name = "id")
    val id: String,

    @param:Json(name = "operatorName")
    val operatorName: String,

    @param:Json(name = "address")
    val address: AddressDto
)

@JsonClass(generateAdapter = true)
internal class DealsItem(

    @param:Json(name = "evseId")
    val evseId: String,

    @param:Json(name = "campaignEndDate")
    val campaignEndDate: String,

    @param:Json(name = "powerSpecification")
    val powerSpecification: PowerSpecification,

    @param:Json(name = "signedDeal")
    val signedDeal: String,

    @param:Json(name = "currency")
    val currency: String,

    @param:Json(name = "id")
    val id: String,

    @param:Json(name = "normalizedEvseId")
    val normalizedEvseId: String,

    @param:Json(name = "pricePerKWh")
    val pricePerKWh: Double,

    @param:Json(name = "expiresAt")
    val expiresAt: String,
)

@JsonClass(generateAdapter = true)
internal class AddressDto(
    @param:Json(name = "streetAddress")
    val streetAddress: List<String>,

    @param:Json(name = "postalCode")
    val postalCode: String,

    @param:Json(name = "locality")
    val locality: String,
)


@JsonClass(generateAdapter = true)
internal class Currency(

    @param:Json(name = "symbol")
    val symbol: String,

    @param:Json(name = "displayName")
    val displayName: String,

    @param:Json(name = "numericCodeAsString")
    val numericCodeAsString: String,

    @param:Json(name = "currencyCode")
    val currencyCode: String,

    @param:Json(name = "defaultFractionDigits")
    val defaultFractionDigits: Int,

    @param:Json(name = "numericCode")
    val numericCode: Int,
)
