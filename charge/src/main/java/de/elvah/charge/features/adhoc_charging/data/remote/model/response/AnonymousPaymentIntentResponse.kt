package de.elvah.charge.features.adhoc_charging.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AnonymousPaymentIntentResponse(

    @Json(name = "data")
    val data: Data,

    @Json(name = "meta")
    val meta: Any?,
)

@JsonClass(generateAdapter = true)
class Data(

    @Json(name = "paymentIntentId")
    val paymentIntentId: String,

    @Json(name = "accountId")
    val accountId: String,

    @Json(name = "ephemeralKey")
    val ephemeralKey: String,

    @Json(name = "customerId")
    val customerId: String,

    @Json(name = "clientSecret")
    val clientSecret: String,
)
