package de.elvah.charge.features.payments.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PaymentSummaryDto(

    @param:Json(name = "data")
    val data: Data,

    @param:Json(name = "meta")
    val meta: Any?
) {

    @JsonClass(generateAdapter = true)
    data class Data(

        @param:Json(name = "address")
        val address: Address,

        @param:Json(name = "totalTimeInSeconds")
        val totalTimeInSeconds: Int,

        @param:Json(name = "sessionStartedAt")
        val sessionStartedAt: String,

        @param:Json(name = "sessionEndedAt")
        val sessionEndedAt: String,

        @param:Json(name = "consumedKWh")
        val consumedKWh: Double,

        @param:Json(name = "totalCost")
        val totalCost: TotalCost
    )

    @JsonClass(generateAdapter = true)
    data class TotalCost(

        @param:Json(name = "amount")
        val amount: Int,

        @param:Json(name = "currency")
        val currency: String
    )

    @JsonClass(generateAdapter = true)
    data class Address(

        @param:Json(name = "streetAddress")
        val streetAddress: String,

        @param:Json(name = "countryCode")
        val countryCode: String,

        @param:Json(name = "postalCode")
        val postalCode: String,

        @param:Json(name = "locality")
        val locality: String
    )
}

