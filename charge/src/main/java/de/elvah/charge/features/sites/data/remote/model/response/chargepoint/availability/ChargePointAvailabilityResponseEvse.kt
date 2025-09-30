package de.elvah.charge.features.sites.data.remote.model.response.chargepoint.availability

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.elvah.charge.features.sites.data.remote.model.response.ChargePointAvailabilityDto

@JsonClass(generateAdapter = true)
internal data class ChargePointAvailabilityResponseEvse(

    @param:Json(name = "evseId")
    val evseId: String,

    @param:Json(name = "normalizedEvseId")
    val normalizedEvseId: String,

    @param:Json(name = "availability")
    val availability: ChargePointAvailabilityDto,
)
