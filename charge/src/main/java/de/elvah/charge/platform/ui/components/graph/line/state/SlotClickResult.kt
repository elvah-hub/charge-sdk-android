package de.elvah.charge.platform.ui.components.graph.line.state

import de.elvah.charge.platform.ui.components.graph.line.DailyPricingData
import de.elvah.charge.platform.ui.components.graph.line.PriceOffer

internal data class SlotClickResult(
    val data: List<DailyPricingData>,
    val price: Double,
    val priceOffer: PriceOffer?,
    val isOffer: Boolean
)
