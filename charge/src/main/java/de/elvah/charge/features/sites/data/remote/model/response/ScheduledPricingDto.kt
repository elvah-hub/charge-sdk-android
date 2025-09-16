package de.elvah.charge.features.sites.data.remote.model.response

import com.squareup.moshi.JsonClass
import com.squareup.moshi.Json

@JsonClass(generateAdapter = true)
data class ScheduledPricingDto(

	@param:Json(name="dailyPricing")
	val dailyPricing: DailyPricingDto,

	@param:Json(name="standardPrice")
	val standardPrice: PriceDto
)

@JsonClass(generateAdapter = true)
data class TimeSlotsItemDto(

	@param:Json(name="isDiscounted")
	val isDiscounted: Boolean,

	@param:Json(name="price")
	val price: PriceDto,

	@param:Json(name="from")
	val from: String,

	@param:Json(name="to")
	val to: String
)

@JsonClass(generateAdapter = true)
data class DailyPricingDto(

	@param:Json(name="yesterday")
	val yesterday: DayDto,

	@param:Json(name="today")
	val today: DayDto,

	@param:Json(name="tomorrow")
	val tomorrow: DayDto
)

@JsonClass(generateAdapter = true)
data class DayDto(

	@param:Json(name="lowestPrice")
	val lowestPrice: PriceDto,

	@param:Json(name="trend")
	val trend: String?,

	@param:Json(name="timeSlots")
	val timeSlots: List<TimeSlotsItemDto>
)
