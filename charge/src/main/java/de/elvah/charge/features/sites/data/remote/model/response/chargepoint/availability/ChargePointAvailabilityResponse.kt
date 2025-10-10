package de.elvah.charge.features.sites.data.remote.model.response.chargepoint.availability

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class ChargePointAvailabilityResponse(

    @param:Json(name = "id")
    val id: String,

    @param:Json(name = "evses")
    val evses: List<ChargePointAvailabilityResponseEvse>,
)
