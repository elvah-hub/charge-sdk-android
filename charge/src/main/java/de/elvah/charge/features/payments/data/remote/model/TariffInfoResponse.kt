package de.elvah.charge.features.payments.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class TariffInfoResponse(

    @Json(name = "data")
    val data: List<DataItem>,

    @Json(name = "meta")
    val meta: Any?,
)

@JsonClass(generateAdapter = true)
class DataItem(

    @Json(name = "evseId")
    val evseId: String,

    @Json(name = "energyTariff")
    val energyTariff: EnergyTariff,

    @Json(name = "timeTariff")
    val timeTariff: TimeTariff?,

    @Json(name = "createdAt")
    val createdAt: String,

    @Json(name = "authorizationAmount")
    val authorizationAmount: AuthorizationAmount,

    @Json(name = "signedOffer")
    val signedOffer: String,

    @Json(name = "currency")
    val currency: String,

    @Json(name = "tariffRuleId")
    val tariffRuleId: String,

    @Json(name = "baseTariff")
    val baseTariff: BaseTariff?,

    @Json(name = "expiresAt")
    val expiresAt: String,
)

@JsonClass(generateAdapter = true)
class EnergyTariff(

    @Json(name = "pricePerKWH")
    val pricePerKWH: Int,
)

@JsonClass(generateAdapter = true)
class AuthorizationAmount(

    @Json(name = "amount")
    val amount: Int,
)

@JsonClass(generateAdapter = true)
class BaseTariff(

    @Json(name = "activeHours")
    val activeHours: Any?,

    @Json(name = "basePrice")
    val basePrice: Int,
)

@JsonClass(generateAdapter = true)
class TimeTariff(

    @Json(name = "pricePerMinute")
    val pricePerMinute: Int,

    @Json(name = "freeMinutesPerSession")
    val freeMinutesPerSession: Int,

    @Json(name = "activeHours")
    val activeHours: Any?,
)
