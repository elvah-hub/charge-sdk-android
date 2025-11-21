package de.elvah.charge.platform.ui.components.graph.line.state

import de.elvah.charge.platform.ui.components.graph.line.PriceOffer

internal data class SelectionState(
    val price: Double,
    val isOffer: Boolean,
    val priceOffer: PriceOffer?
)
