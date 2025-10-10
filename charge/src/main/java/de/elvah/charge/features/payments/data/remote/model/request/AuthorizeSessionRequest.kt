package de.elvah.charge.features.payments.data.remote.model.request

import com.squareup.moshi.Json

internal class AuthorizeSessionRequest(
    @param:Json(name = "paymentId") val paymentId: String,
)
