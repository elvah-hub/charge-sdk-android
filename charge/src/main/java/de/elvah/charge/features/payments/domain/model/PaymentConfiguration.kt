package de.elvah.charge.features.payments.domain.model

internal class PaymentConfiguration(
    val publishableKey: PublishableKey,
    val clientSecret: String,
    val accountId: String,
    val paymentId: String
)
