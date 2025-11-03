package de.elvah.charge.features.payments.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal class TariffInfoResponse(

    @param:Json(name = "data")
    val data: List<DataItem>,

    @param:Json(name = "meta")
    val meta: Any?,
)

@JsonClass(generateAdapter = true)
internal class DataItem(

    @param:Json(name = "evseId")
    val evseId: String,

    @param:Json(name = "energyTariff")
    val energyTariff: EnergyTariff,

    @param:Json(name = "timeTariff")
    val timeTariff: TimeTariff?,

    @param:Json(name = "createdAt")
    val createdAt: String,

    @param:Json(name = "authorizationAmount")
    val authorizationAmount: AuthorizationAmount,

    @param:Json(name = "signedOffer")
    val signedOffer: String,

    @param:Json(name = "currency")
    val currency: String,

    @param:Json(name = "tariffRuleId")
    val tariffRuleId: String,

    @param:Json(name = "baseTariff")
    val baseTariff: BaseTariff?,

    @param:Json(name = "expiresAt")
    val expiresAt: String,
)

@JsonClass(generateAdapter = true)
internal class EnergyTariff(

    @param:Json(name = "pricePerKWH")
    val pricePerKWH: Int,
)

@JsonClass(generateAdapter = true)
internal class AuthorizationAmount(

    @param:Json(name = "amount")
    val amount: Int,
)

@JsonClass(generateAdapter = true)
internal class BaseTariff(

    @param:Json(name = "activeHours")
    val activeHours: Any?,

    @param:Json(name = "basePrice")
    val basePrice: Int,
)

@JsonClass(generateAdapter = true)
internal class TimeTariff(

    @param:Json(name = "pricePerMinute")
    val pricePerMinute: Int,

    @param:Json(name = "freeMinutesPerSession")
    val freeMinutesPerSession: Int,

    @param:Json(name = "activeHours")
    val activeHours: Any?,
)
