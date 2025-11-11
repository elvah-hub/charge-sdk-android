package de.elvah.charge.components.pricinggraph

import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphEffect
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow

internal class PricingGraphComponentSourcePreview : PricingGraphComponentSource {

    private val _state = MutableStateFlow<PricingGraphState>(getSuccessState())
    override val state: StateFlow<PricingGraphState> = _state

    private val _effects: MutableSharedFlow<PricingGraphEffect> = MutableSharedFlow()
    override val effects: SharedFlow<PricingGraphEffect> = _effects.asSharedFlow()

    override fun loadPricing(siteId: String) {
        setSuccessState()
    }

    override fun refreshData() {
        setSuccessState()
    }

    override fun retryLoad() {
        setSuccessState()
    }

    private fun setSuccessState() {
        _state.value = getSuccessState()
    }

    private fun getSuccessState() = PricingGraphState.Success(
        siteId = "testing",
        scheduledPricing = PricingGraphComponentSourcePreviewMock.scheduledPricingMock,
        siteDetail = PricingGraphComponentSourcePreviewMock.siteDetailMock,
    )
}
