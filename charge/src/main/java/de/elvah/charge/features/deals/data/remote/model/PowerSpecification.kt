package de.elvah.charge.features.deals.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class PowerSpecification(

    @param:Json(name = "maxPowerInKW")
    val maxPowerInKW: Int,

    @param:Json(name = "type")
    val type: String, // AC/DC
)
