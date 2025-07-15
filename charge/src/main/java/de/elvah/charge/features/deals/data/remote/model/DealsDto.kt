package de.elvah.charge.features.deals.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class DealsDto(

    @Json(name = "data")
    val data: List<CPOItem>,

    @Json(name = "meta")
    val meta: Meta?,
)

@JsonClass(generateAdapter = true)
internal class CPOItem(

    @Json(name = "deals")
    val deals: List<DealsItem>,

    @Json(name = "location")
    val location: List<Double>,

    @Json(name = "id")
    val id: String,

    @Json(name = "operatorName")
    val operatorName: String,

    @Json(name = "address")
    val address: AddressDto
)

@JsonClass(generateAdapter = true)
internal class DealsItem(

    @Json(name = "evseId")
    val evseId: String,

    @Json(name = "campaignEndDate")
    val campaignEndDate: String,

    @Json(name = "powerSpecification")
    val powerSpecification: PowerSpecification,

    @Json(name = "signedDeal")
    val signedDeal: String,

    @Json(name = "currency")
    val currency: String,

    @Json(name = "id")
    val id: String,

    @Json(name = "normalizedEvseId")
    val normalizedEvseId: String,

    @Json(name = "pricePerKWh")
    val pricePerKWh: Double,

    @Json(name = "expiresAt")
    val expiresAt: String,
)

@JsonClass(generateAdapter = true)
internal class AddressDto(
    @Json(name = "streetAddress")
    val streetAddress: List<String>,

    @Json(name = "postalCode")
    val postalCode: String,

    @Json(name = "locality")
    val locality: String,
)


@JsonClass(generateAdapter = true)
internal class Currency(

    @Json(name = "symbol")
    val symbol: String,

    @Json(name = "displayName")
    val displayName: String,

    @Json(name = "numericCodeAsString")
    val numericCodeAsString: String,

    @Json(name = "currencyCode")
    val currencyCode: String,

    @Json(name = "defaultFractionDigits")
    val defaultFractionDigits: Int,

    @Json(name = "numericCode")
    val numericCode: Int,
)
