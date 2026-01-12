package de.elvah.charge.public_api.pricinggraph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphEffect
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphState
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphViewModel
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphContent
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphEmpty
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphError
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphLoading
import de.elvah.charge.features.sites.ui.utils.openSite
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import de.elvah.charge.public_api.DisplayBehavior
import kotlinx.coroutines.flow.collectLatest
import de.elvah.charge.platform.di.sdkGet
import de.elvah.charge.platform.di.sdkViewModel

@Composable
public fun PricingGraph(
    siteId: String,
    modifier: Modifier = Modifier,
    display: DisplayBehavior = DisplayBehavior.WHEN_SOURCE_SET,
    colors: GraphColors = GraphColorDefaults.colors(),
    onError: ((String) -> Unit)? = null,
    onRefreshSuccess: (() -> Unit)? = null,
    minYAxisPrice: Double? = null,
    gridLineDotSize: Float = 4f
) {
    val pricingGraphViewModel: PricingGraphViewModel = sdkViewModel()
    val config: Config = sdkGet()

    val context = LocalContext.current

    val state by pricingGraphViewModel.state.collectAsStateWithLifecycle()

    // Handle effects
    LaunchedEffect(pricingGraphViewModel) {
        pricingGraphViewModel.effects.collectLatest { effect ->
            when (effect) {
                is PricingGraphEffect.ShowErrorToast -> {
                    onError?.invoke(effect.message)
                }

                PricingGraphEffect.ShowRefreshSuccessToast -> {
                    onRefreshSuccess?.invoke()
                }

                PricingGraphEffect.ShowLoadingIndicator -> {
                    // Could trigger additional loading UI if needed
                }

                PricingGraphEffect.HideLoadingIndicator -> {
                    // Could hide additional loading UI if needed
                }

                is PricingGraphEffect.NavigateToErrorScreen -> {
                    // Could trigger navigation if needed
                }
            }
        }
    }

    // Load pricing data when siteId changes
    LaunchedEffect(siteId) {
        pricingGraphViewModel.loadPricing(siteId)
    }

    ElvahChargeTheme(
        darkTheme = shouldUseDarkColors(config.darkTheme),
        customLightColorScheme = config.customLightColorScheme,
        customDarkColorScheme = config.customDarkColorScheme
    ) {
        when (val currentState = state) {
            is PricingGraphState.Loading -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    PricingGraphLoading(modifier)
                }
            }

            is PricingGraphState.Success -> {
                PricingGraphContent(
                    scheduledPricing = currentState.scheduledPricing,
                    chargeSite = currentState.siteDetail,
                    modifier = modifier,
                    colors = colors,
                    minYAxisPrice = minYAxisPrice,
                    gridLineDotSize = gridLineDotSize,
                    onChargeNowClick = {
                        context.openSite(siteId)
                    }
                )
            }

            is PricingGraphState.Error -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    PricingGraphError(
                        onRetry = {
                            //pricingGraphViewModel.retryLoad()
                        },
                        modifier = modifier
                    )
                }
            }

            is PricingGraphState.Empty -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    PricingGraphEmpty(modifier)
                }
            }
        }
    }
}
