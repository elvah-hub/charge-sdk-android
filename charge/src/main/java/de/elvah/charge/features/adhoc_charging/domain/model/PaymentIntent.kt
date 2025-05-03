package de.elvah.charge.features.adhoc_charging.domain.model

class PaymentIntent(
    val clientSecret: String,
    val customerId: String,
    val ephemeralKeySecret: String,
    val accountId: String,
)