package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class BlockingFeeDto(

    @param:Json(name = "pricePerMinute")
    val pricePerMinute: Double,

    @param:Json(name = "startsAfterMinutes")
    val startsAfterMinutes: Int,

    @param:Json(name = "maxAmount")
    val maxAmount: Double?,

    @param:Json(name = "timeSlots")
    val timeSlots: List<TimeSlotDto>?,
)

@JsonClass(generateAdapter = true)
internal data class TimeSlotDto(
    @param:Json(name = "startTime")
    val startTime: String,

    @param:Json(name = "endTime")
    val endTime: String,
)
