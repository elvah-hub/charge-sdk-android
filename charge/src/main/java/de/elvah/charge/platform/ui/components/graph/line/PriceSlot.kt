package de.elvah.charge.platform.ui.components.graph.line

import java.time.LocalTime

internal sealed class PriceSlot {
    abstract val startTime: LocalTime
    abstract val endTime: LocalTime
    abstract val price: Double
    abstract val isSelected: Boolean

    data class RegularPriceSlot(
        override val startTime: LocalTime,
        override val endTime: LocalTime,
        override val price: Double,
        override val isSelected: Boolean = false
    ) : PriceSlot()

    data class OfferPriceSlot(
        override val startTime: LocalTime,
        override val endTime: LocalTime,
        override val price: Double,
        val originalPrice: Double,
        override val isSelected: Boolean = false
    ) : PriceSlot()
}
