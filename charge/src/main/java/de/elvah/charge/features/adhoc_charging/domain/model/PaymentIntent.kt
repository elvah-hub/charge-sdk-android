package de.elvah.charge.features.adhoc_charging.domain.model

internal class PaymentIntent(
    val clientSecret: String,
    val customerId: String,
    val ephemeralKeySecret: String,
    val accountId: String,
)
