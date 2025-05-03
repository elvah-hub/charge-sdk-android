package de.elvah.charge.features.adhoc_charging.data.remote.model.request

class AnonymousPaymentIntentRequest(
    val signedOffer: String,
    val countryOfResidence: String,
)