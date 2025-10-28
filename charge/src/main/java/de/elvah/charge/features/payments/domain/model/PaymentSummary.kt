package de.elvah.charge.features.payments.domain.model

internal class PaymentSummary(
    val evseId: String,
    val cpoName: String,
    val logoUrl: String,
    val address: String,
    val totalTime: Int,
    val consumedKWh: Double,
    val totalCost: Int,
)