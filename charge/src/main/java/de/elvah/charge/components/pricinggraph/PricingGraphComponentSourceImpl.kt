package de.elvah.charge.components.pricinggraph

import de.elvah.charge.components.sitessource.InternalSitesSource
import de.elvah.charge.features.sites.ui.mapper.toUI
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphEffect
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphEvent
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphState
import de.elvah.charge.features.sites.ui.pricinggraph.mapper.toUI
import de.elvah.charge.platform.core.mvi.MVIBase
import de.elvah.charge.platform.core.mvi.Reducer
import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

internal class PricingGraphComponentSourceImpl(
    private val coroutineScope: CoroutineScope,
    sitesSource: SitesSource,
) : PricingGraphComponentSource,
    MVIBase<PricingGraphState, PricingGraphEvent, PricingGraphEffect>(
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
                            scheduledPricing = event.scheduledPricing,
                            siteDetail = event.siteDetail,

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
                            scheduledPricing = event.scheduledPricing,
                            siteDetail = event.siteDetail
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

    private val internalSource = sitesSource as InternalSitesSource

    override fun loadPricing(siteId: String) {
        // TODO: avoid setting loading state if we are not loading after all (check cache first)
        sendEvent(PricingGraphEvent.LoadPricing(siteId), allowSideEffect = true)
        executeLoadPricing(siteId)
    }

    override fun refreshData() {
        sendEvent(PricingGraphEvent.RefreshData, allowSideEffect = true)
        when (val currentState = state.value) {
            is PricingGraphState.Success -> executeLoadPricing(
                currentState.siteId,
                isRefresh = true
            )

            is PricingGraphState.Error -> currentState.siteId?.let {
                executeLoadPricing(
                    it,
                    isRefresh = true
                )
            }

            is PricingGraphState.Empty -> currentState.siteId?.let {
                executeLoadPricing(
                    it,
                    isRefresh = true
                )
            }

            is PricingGraphState.Loading -> currentState.siteId?.let {
                executeLoadPricing(
                    it,
                    isRefresh = true
                )
            }
        }
    }

    override fun retryLoad() {
        sendEvent(PricingGraphEvent.RetryLoad, allowSideEffect = true)
        when (val currentState = state.value) {
            is PricingGraphState.Error -> currentState.siteId?.let { executeLoadPricing(it) }
            is PricingGraphState.Empty -> currentState.siteId?.let { executeLoadPricing(it) }
            // No retry needed for other states
            else -> {}
        }
    }

    private fun executeLoadPricing(siteId: String, isRefresh: Boolean = false) {
        coroutineScope.launch {
            internalSource.getSiteScheduledPricing(siteId).fold(
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
                    val siteDetail = internalSource.getSite(siteId)

                    siteDetail.fold(
                        ifLeft = { throwable ->
                            sendEvent(
                                PricingGraphEvent.LoadPricingError(siteId, throwable),
                                allowSideEffect = true
                            )
                        },
                        ifRight = { chargeSite ->
                            if (isRefresh) {
                                sendEvent(
                                    PricingGraphEvent.RefreshPricingSuccess(
                                        siteId,
                                        scheduledPricingUI,
                                        chargeSite.toUI()
                                    ),
                                    allowSideEffect = true
                                )
                            } else {
                                sendEvent(
                                    PricingGraphEvent.LoadPricingSuccess(
                                        siteId,
                                        scheduledPricingUI,
                                        chargeSite.toUI()
                                    ),
                                    allowSideEffect = true
                                )
                            }
                        }
                    )
                }
            )
        }
    }
}
