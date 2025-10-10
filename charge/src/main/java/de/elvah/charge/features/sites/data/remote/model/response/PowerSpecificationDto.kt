package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json

internal data class PowerSpecificationDto(

    @param:Json(name = "maxPowerInKW")
    val maxPowerInKW: Float?,

    @param:Json(name = "type")
    val type: String
)
