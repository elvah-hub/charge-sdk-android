package de.elvah.charge.features.sites.ui.pricinggraph

import de.elvah.charge.features.sites.ui.pricinggraph.model.ScheduledPricingUI
import de.elvah.charge.platform.core.mvi.Reducer

internal sealed class PricingGraphEvent : Reducer.ViewEvent {
    // User-initiated events
    data class LoadPricing(val siteId: String) : PricingGraphEvent()
    data object RefreshData : PricingGraphEvent()
    data object RetryLoad : PricingGraphEvent()

    // Result events from use cases
    data class LoadPricingSuccess(
        val siteId: String,
        val scheduledPricing: ScheduledPricingUI
    ) : PricingGraphEvent()

    data class LoadPricingError(
        val siteId: String,
        val error: Throwable
    ) : PricingGraphEvent()

    data class LoadPricingEmpty(
        val siteId: String
    ) : PricingGraphEvent()

    data class RefreshPricingSuccess(
        val siteId: String,
        val scheduledPricing: ScheduledPricingUI
    ) : PricingGraphEvent()

    data class RefreshPricingError(
        val siteId: String,
        val error: Throwable
    ) : PricingGraphEvent()
}
