package de.elvah.charge.features.sites.ui.pricinggraph

import de.elvah.charge.features.sites.ui.pricinggraph.model.ScheduledPricingUI

internal sealed class PricingGraphState {
    data object Loading : PricingGraphState()
    data class Success(val scheduledPricing: ScheduledPricingUI) : PricingGraphState()
    data object Error : PricingGraphState()
    data object Empty : PricingGraphState()
}