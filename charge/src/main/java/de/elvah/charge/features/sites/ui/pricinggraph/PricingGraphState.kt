package de.elvah.charge.features.sites.ui.pricinggraph

import de.elvah.charge.features.sites.ui.pricinggraph.model.ScheduledPricingUI
import de.elvah.charge.platform.core.mvi.Reducer

internal sealed class PricingGraphState : Reducer.ViewState {
    data class Loading(val siteId: String?) : PricingGraphState()
    data class Success(
        val siteId: String,
        val scheduledPricing: ScheduledPricingUI
    ) : PricingGraphState()

    data class Error(
        val siteId: String?,
        val message: String
    ) : PricingGraphState()

    data class Empty(val siteId: String?) : PricingGraphState()
}
