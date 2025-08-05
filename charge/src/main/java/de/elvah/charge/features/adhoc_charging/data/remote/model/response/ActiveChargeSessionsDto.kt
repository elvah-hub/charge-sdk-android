package de.elvah.charge.features.adhoc_charging.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ActiveChargeSessionsDto(
    @param:Json(name = "data")
    val data: Data,
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(

        @param:Json(name = "evseId")
        val evseId: String,

        @param:Json(name = "duration")
        val duration: Int?,

        @param:Json(name = "consumption")
        val consumption: Double?,

        @param:Json(name = "status")
        val status: String,
    )
}

