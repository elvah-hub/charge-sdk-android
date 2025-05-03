package de.elvah.charge.features.payments.domain.model

internal class PaymentIntentValues(
    val clientSecret: String,
    val customerId: String,
    val ephemeralKeySecret: String,
    val accountId: String,
)