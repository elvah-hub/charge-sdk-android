package de.elvah.charge.features.payments.data.remote.model.request

import com.squareup.moshi.Json

class AuthorizeSessionRequest(
    @Json(name = "paymentId") val paymentId: String,
)