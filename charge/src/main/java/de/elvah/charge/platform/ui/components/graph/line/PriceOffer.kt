package de.elvah.charge.platform.ui.components.graph.line

data class PriceOffer(
    val timeRange: TimeRange,
    val discountedPrice: Double,
    val isSelected: Boolean = false // Future selection state
)
