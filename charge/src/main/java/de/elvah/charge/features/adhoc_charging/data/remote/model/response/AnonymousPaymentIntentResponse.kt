package de.elvah.charge.features.adhoc_charging.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AnonymousPaymentIntentResponse(

    @param:Json(name = "data")
    val data: Data,

    @param:Json(name = "meta")
    val meta: Any?,
)

@JsonClass(generateAdapter = true)
class Data(

    @param:Json(name = "paymentIntentId")
    val paymentIntentId: String,

    @param:Json(name = "accountId")
    val accountId: String,

    @param:Json(name = "ephemeralKey")
    val ephemeralKey: String,

    @param:Json(name = "customerId")
    val customerId: String,

    @param:Json(name = "clientSecret")
    val clientSecret: String,
)
