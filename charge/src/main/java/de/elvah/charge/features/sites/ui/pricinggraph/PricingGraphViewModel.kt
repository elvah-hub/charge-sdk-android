package de.elvah.charge.features.sites.ui.pricinggraph

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.sites.domain.usecase.GetSiteScheduledPricing
import de.elvah.charge.features.sites.ui.pricinggraph.mapper.toUI
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class PricingGraphViewModel(
    private val getSiteScheduledPricing: GetSiteScheduledPricing
) : ViewModel() {

    private val _state = MutableStateFlow<PricingGraphState>(PricingGraphState.Loading)
    val state: StateFlow<PricingGraphState> = _state.asStateFlow()

    private val _effects = Channel<PricingGraphEffect>()
    val effects = _effects.receiveAsFlow()

    private var currentSiteId: String? = null

    fun handleIntent(intent: PricingGraphIntent) {
        when (intent) {
            is PricingGraphIntent.LoadPricing -> {
                currentSiteId = intent.siteId
                loadPricing(intent.siteId)
            }
            PricingGraphIntent.RefreshData -> {
                currentSiteId?.let { siteId ->
                    loadPricing(siteId, isRefresh = true)
                }
            }
            PricingGraphIntent.RetryLoad -> {
                currentSiteId?.let { siteId ->
                    loadPricing(siteId)
                }
            }
        }
    }

    private fun loadPricing(siteId: String, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh) {
                _state.value = PricingGraphState.Loading
            }

            getSiteScheduledPricing(GetSiteScheduledPricing.Params(siteId = siteId))
                .fold(
                    ifLeft = { throwable ->
                        _state.value = PricingGraphState.Error
                        _effects.send(PricingGraphEffect.ShowError(
                            throwable.message ?: "Failed to load pricing data"
                        ))
                    },
                    ifRight = { scheduledPricing ->
                        _state.value = PricingGraphState.Success(scheduledPricing.toUI())
                        if (isRefresh) {
                            _effects.send(PricingGraphEffect.ShowRefreshSuccess)
                        }
                    }
                )
        }
    }
}