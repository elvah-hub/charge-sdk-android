package de.elvah.charge.features.sites.ui.pricinggraph

internal sealed class PricingGraphIntent {
    data class LoadPricing(val siteId: String) : PricingGraphIntent()
    data object RefreshData : PricingGraphIntent()
    data object RetryLoad : PricingGraphIntent()
}