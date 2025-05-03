package de.elvah.charge.features.payments.domain.model

internal class PaymentIntent(
    val paymentId: String,
    val paymentIntentId: String,
    val accountId: String,
    val clientSecret: String,
    val amount: Double,
    val currency: String,
)