package de.elvah.charge.features.adhoc_charging.ui.screens.review.model

class PaymentSummaryUI(
    val evseId: String,
    val cpoName: String,
    val address: String,
    val totalTime: String,
    val consumedKWh: Double,
    val cpoLogo: String,
    val totalCost: Double,
)