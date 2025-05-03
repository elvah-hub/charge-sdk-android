package de.elvah.charge.features.payments.domain.model

internal class PaymentSummary(
    val evseId: String,
    val cpoName: String,
    val address: String,
    val totalTime: String,
    val consumedKWh: Double,
    val totalCost: Int,
)