package de.elvah.charge.platform.ui.components.graph.line.state

import androidx.compose.runtime.MutableState
import de.elvah.charge.platform.ui.components.graph.line.ChargeType
import de.elvah.charge.platform.ui.components.graph.line.DailyPricingData
import de.elvah.charge.platform.ui.components.graph.line.PriceOffer

internal data class ChartState(
    val updatedDailyData: MutableState<List<DailyPricingData>>,
    val selectedPrice: MutableState<Double>,
    val offerSelected: MutableState<Boolean>,
    val selectedPriceOffer: MutableState<PriceOffer?>,
    val selectedType: MutableState<ChargeType>
)
