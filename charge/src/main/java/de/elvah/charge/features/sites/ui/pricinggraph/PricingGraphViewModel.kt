package de.elvah.charge.features.sites.ui.pricinggraph

import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.sites.domain.usecase.GetSiteScheduledPricing
import de.elvah.charge.features.sites.ui.pricinggraph.mapper.toUI
import de.elvah.charge.platform.core.mvi.MVIBaseViewModel
import de.elvah.charge.platform.core.mvi.Reducer
import kotlinx.coroutines.launch

internal class PricingGraphViewModel(
    private val getSiteScheduledPricing: GetSiteScheduledPricing
) : MVIBaseViewModel<PricingGraphState, PricingGraphEvent, PricingGraphEffect>(
    initialState = PricingGraphState.Loading(null),
    reducer = Reducer { previousState, event ->
        when (event) {
            // User-initiated events
            is PricingGraphEvent.LoadPricing -> {
                Reducer.Result(PricingGraphState.Loading(event.siteId), null)
            }
            
            PricingGraphEvent.RefreshData -> when (previousState) {
                is PricingGraphState.Loading -> Reducer.Result(previousState, null)
                is PricingGraphState.Success -> Reducer.Result(
                    PricingGraphState.Loading(previousState.siteId), 
                    PricingGraphEffect.ShowLoadingIndicator
                )
                is PricingGraphState.Error -> Reducer.Result(
                    PricingGraphState.Loading(previousState.siteId), 
                    null
                )
                is PricingGraphState.Empty -> Reducer.Result(
                    PricingGraphState.Loading(previousState.siteId), 
                    null
                )
            }
            
            PricingGraphEvent.RetryLoad -> when (previousState) {
                is PricingGraphState.Loading -> Reducer.Result(previousState, null)
                is PricingGraphState.Success -> Reducer.Result(previousState, null)
                is PricingGraphState.Error -> Reducer.Result(
                    PricingGraphState.Loading(previousState.siteId), 
                    null
                )
                is PricingGraphState.Empty -> Reducer.Result(
                    PricingGraphState.Loading(previousState.siteId), 
                    null
                )
            }
            
            // Result events from use cases
            is PricingGraphEvent.LoadPricingSuccess -> {
                Reducer.Result(
                    PricingGraphState.Success(
                        siteId = event.siteId,
                        scheduledPricing = event.scheduledPricing
                    ),
                    PricingGraphEffect.HideLoadingIndicator
                )
            }
            
            is PricingGraphEvent.LoadPricingError -> {
                Reducer.Result(
                    PricingGraphState.Error(
                        siteId = event.siteId,
                        message = event.error.message ?: "Failed to load pricing data"
                    ),
                    PricingGraphEffect.ShowErrorToast(
                        event.error.message ?: "Failed to load pricing data"
                    )
                )
            }
            
            is PricingGraphEvent.LoadPricingEmpty -> {
                Reducer.Result(
                    PricingGraphState.Empty(event.siteId),
                    PricingGraphEffect.HideLoadingIndicator
                )
            }
            
            is PricingGraphEvent.RefreshPricingSuccess -> {
                Reducer.Result(
                    PricingGraphState.Success(
                        siteId = event.siteId,
                        scheduledPricing = event.scheduledPricing
                    ),
                    PricingGraphEffect.ShowRefreshSuccessToast
                )
            }
            
            is PricingGraphEvent.RefreshPricingError -> {
                // Keep the previous state but show error toast
                Reducer.Result(
                    previousState,
                    PricingGraphEffect.ShowErrorToast(
                        event.error.message ?: "Failed to refresh pricing data"
                    )
                )
            }
        }
    }
) {
    
    fun loadPricing(siteId: String) {
        sendEvent(PricingGraphEvent.LoadPricing(siteId), allowSideEffect = true)
        executeLoadPricing(siteId)
    }
    
    fun refreshData() {
        sendEvent(PricingGraphEvent.RefreshData, allowSideEffect = true)
        when (val currentState = state.value) {
            is PricingGraphState.Success -> executeLoadPricing(currentState.siteId, isRefresh = true)
            is PricingGraphState.Error -> currentState.siteId?.let { executeLoadPricing(it, isRefresh = true) }
            is PricingGraphState.Empty -> currentState.siteId?.let { executeLoadPricing(it, isRefresh = true) }
            is PricingGraphState.Loading -> currentState.siteId?.let { executeLoadPricing(it, isRefresh = true) }
        }
    }
    
    fun retryLoad() {
        sendEvent(PricingGraphEvent.RetryLoad, allowSideEffect = true)
        when (val currentState = state.value) {
            is PricingGraphState.Error -> currentState.siteId?.let { executeLoadPricing(it) }
            is PricingGraphState.Empty -> currentState.siteId?.let { executeLoadPricing(it) }
            else -> { /* No retry needed for other states */ }
        }
    }
    
    private fun executeLoadPricing(siteId: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            getSiteScheduledPricing(GetSiteScheduledPricing.Params(siteId = siteId))
                .fold(
                    ifLeft = { throwable ->
                        if (isRefresh) {
                            sendEvent(
                                PricingGraphEvent.RefreshPricingError(siteId, throwable),
                                allowSideEffect = true
                            )
                        } else {
                            sendEvent(
                                PricingGraphEvent.LoadPricingError(siteId, throwable),
                                allowSideEffect = true
                            )
                        }
                    },
                    ifRight = { scheduledPricing ->
                        val scheduledPricingUI = scheduledPricing.toUI()
                        if (isRefresh) {
                            sendEvent(
                                PricingGraphEvent.RefreshPricingSuccess(siteId, scheduledPricingUI),
                                allowSideEffect = true
                            )
                        } else {
                            sendEvent(
                                PricingGraphEvent.LoadPricingSuccess(siteId, scheduledPricingUI),
                                allowSideEffect = true
                            )
                        }
                    }
                )
        }
    }
}