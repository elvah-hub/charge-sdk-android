package de.elvah.charge.features.payments.data.remote.model.request

import com.squareup.moshi.Json

class CreatePaymentIntentRequest(
    @Json(name = "signedOffer") val signedOffer: String,
)