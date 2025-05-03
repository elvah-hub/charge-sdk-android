package de.elvah.charge.features.adhoc_charging.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ActiveChargeSessionsDto(
    @Json(name = "data")
    val data: Data,
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(

        @Json(name = "evseId")
        val evseId: String,

        @Json(name = "duration")
        val duration: Int?,

        @Json(name = "consumption")
        val consumption: Double?,

        @Json(name = "status")
        val status: String,
    )
}

