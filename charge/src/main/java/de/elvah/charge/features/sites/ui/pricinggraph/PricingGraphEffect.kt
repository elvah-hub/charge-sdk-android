package de.elvah.charge.features.sites.ui.pricinggraph

internal sealed class PricingGraphEffect {
    data class ShowError(val message: String) : PricingGraphEffect()
    data object ShowRefreshSuccess : PricingGraphEffect()
}