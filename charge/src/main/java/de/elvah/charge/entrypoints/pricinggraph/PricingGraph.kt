package de.elvah.charge.entrypoints.pricinggraph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.entrypoints.DisplayBehavior
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphEffect
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphIntent
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphState
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphViewModel
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphContent
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphEmpty
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphError
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphLoading
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PricingGraph(
    siteId: String,
    modifier: Modifier = Modifier,
    display: DisplayBehavior = DisplayBehavior.WHEN_SOURCE_SET,
    onError: ((String) -> Unit)? = null,
    onRefreshSuccess: (() -> Unit)? = null,
) {
    val pricingGraphViewModel: PricingGraphViewModel = koinViewModel()
    val config: Config = koinInject()

    val state by pricingGraphViewModel.state.collectAsStateWithLifecycle()

    // Handle effects
    LaunchedEffect(pricingGraphViewModel) {
        pricingGraphViewModel.effects.collectLatest { effect ->
            when (effect) {
                is PricingGraphEffect.ShowError -> {
                    onError?.invoke(effect.message)
                }
                PricingGraphEffect.ShowRefreshSuccess -> {
                    onRefreshSuccess?.invoke()
                }
            }
        }
    }

    // Load pricing data when siteId changes
    LaunchedEffect(siteId) {
        pricingGraphViewModel.handleIntent(PricingGraphIntent.LoadPricing(siteId))
    }

    ElvahChargeTheme(darkTheme = shouldUseDarkColors(config.darkTheme)) {
        when (val currentState = state) {
            PricingGraphState.Loading -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    PricingGraphLoading(modifier)
                }
            }

            is PricingGraphState.Success -> {
                PricingGraphContent(
                    scheduledPricing = currentState.scheduledPricing,
                    modifier = modifier
                )
            }

            PricingGraphState.Error -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    PricingGraphError(
                        onRetry = {
                            pricingGraphViewModel.handleIntent(PricingGraphIntent.RetryLoad)
                        },
                        modifier = modifier
                    )
                }
            }

            PricingGraphState.Empty -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    PricingGraphEmpty(modifier)
                }
            }
        }
    }
}

@Composable
fun RefreshablePricingGraph(
    siteId: String,
    modifier: Modifier = Modifier,
    display: DisplayBehavior = DisplayBehavior.WHEN_SOURCE_SET,
    onError: ((String) -> Unit)? = null,
    onRefreshSuccess: (() -> Unit)? = null,
    onRefresh: (() -> Unit)? = null,
) {
    val pricingGraphViewModel: PricingGraphViewModel = koinViewModel()

    LaunchedEffect(onRefresh) {
        onRefresh?.let {
            it.invoke()
            pricingGraphViewModel.handleIntent(PricingGraphIntent.RefreshData)
        }
    }

    PricingGraph(
        siteId = siteId,
        modifier = modifier,
        display = display,
        onError = onError,
        onRefreshSuccess = onRefreshSuccess
    )
}