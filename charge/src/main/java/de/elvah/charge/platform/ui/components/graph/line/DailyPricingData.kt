package de.elvah.charge.platform.ui.components.graph.line

import java.time.LocalDate

data class DailyPricingData(
    val date: LocalDate,
    val regularPrice: Double,
    val offers: List<PriceOffer> = emptyList(),
    val currency: String = "€",
    val isSelected: Boolean = false // Future selection state for entire day
)
