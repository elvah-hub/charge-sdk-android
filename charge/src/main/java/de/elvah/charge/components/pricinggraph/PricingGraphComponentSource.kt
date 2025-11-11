package de.elvah.charge.components.pricinggraph

import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphEffect
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphState
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

internal interface PricingGraphComponentSource {

    val state: StateFlow<PricingGraphState>

    val effects: SharedFlow<PricingGraphEffect>

    fun loadPricing(siteId: String)

    fun refreshData()

    fun retryLoad()
}
