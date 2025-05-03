package de.elvah.charge.features.payments.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentSummaryDto(

	@Json(name="data")
	val data: Data,

	@Json(name="meta")
	val meta: Any?
){

	@JsonClass(generateAdapter = true)
	data class Data(

		@Json(name="address")
		val address: Address,

		@Json(name="totalTime")
		val totalTime: String,

		@Json(name="sessionStartedAt")
		val sessionStartedAt: String,

		@Json(name="sessionEndedAt")
		val sessionEndedAt: String,

		@Json(name="consumedKWh")
		val consumedKWh: Double,

		@Json(name="totalCost")
		val totalCost: TotalCost
	)

	@JsonClass(generateAdapter = true)
	data class TotalCost(

		@Json(name="amount")
		val amount: Int,

		@Json(name="currency")
		val currency: String
	)

	@JsonClass(generateAdapter = true)
	data class Address(

		@Json(name="streetAddress")
		val streetAddress: String,

		@Json(name="countryCode")
		val countryCode: String,

		@Json(name="postalCode")
		val postalCode: String,

		@Json(name="locality")
		val locality: String
	)
}

