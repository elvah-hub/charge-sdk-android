package de.elvah.charge.features.sites.ui.pricinggraph

import de.elvah.charge.platform.core.mvi.Reducer

internal sealed class PricingGraphEffect : Reducer.ViewEffect {
    data class ShowErrorToast(val message: String) : PricingGraphEffect()
    data object ShowRefreshSuccessToast : PricingGraphEffect()
    data object ShowLoadingIndicator : PricingGraphEffect()
    data object HideLoadingIndicator : PricingGraphEffect()
    data class NavigateToErrorScreen(val siteId: String, val errorMessage: String) : PricingGraphEffect()
}