package de.elvah.charge.features.payments.data.remote.model.request

import com.squareup.moshi.Json

class CreatePaymentIntentRequest(
    @param:Json(name = "signedOffer") val signedOffer: String,
)
