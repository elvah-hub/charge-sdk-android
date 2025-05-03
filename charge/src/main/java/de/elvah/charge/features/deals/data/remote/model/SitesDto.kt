package de.elvah.charge.features.deals.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SitesDto(

    @Json(name = "data")
    val data: List<CPOItem>,

    @Json(name = "meta")
    val meta: Meta?
)

@JsonClass(generateAdapter = true)
internal data class DataItem(

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "lastModifiedAt")
    val lastModifiedAt: String,

    @Json(name = "location")
    val location: List<Int>,

    @Json(name = "id")
    val id: String,

    @Json(name = "evseIdPrefix")
    val evseIdPrefix: String,

    @Json(name = "operatorName")
    val operatorName: String,

    @Json(name = "prevalentPowerType")
    val prevalentPowerType: String,

    @Json(name = "chargePoints")
    val chargePoints: List<ChargePointsItem>
)


@JsonClass(generateAdapter = true)
internal data class ChargePointsItem(

    @Json(name = "evseId")
    val evseId: String,

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "lastModifiedAt")
    val lastModifiedAt: String,

    @Json(name = "powerSpecification")
    val powerSpecification: PowerSpecification,

    @Json(name = "location")
    val location: List<Int>,

    @Json(name = "id")
    val id: String,

    @Json(name = "operatorName")
    val operatorName: String
)