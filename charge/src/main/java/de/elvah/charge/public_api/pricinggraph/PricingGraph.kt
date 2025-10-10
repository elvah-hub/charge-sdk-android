package de.elvah.charge.public_api.pricinggraph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.components.common.DisplayContent
import de.elvah.charge.components.pricinggraph.rememberPricingGraphSource
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphEffect
import de.elvah.charge.features.sites.ui.pricinggraph.PricingGraphState
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphContent
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphEmpty
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphError
import de.elvah.charge.features.sites.ui.pricinggraph.components.PricingGraphLoading
import de.elvah.charge.features.sites.ui.utils.openSite
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import de.elvah.charge.public_api.DisplayBehavior
import de.elvah.charge.public_api.sitessource.SitesSource
import de.elvah.charge.public_api.sitessource.rememberSitesSource
import kotlinx.coroutines.flow.collectLatest

@Composable
public fun PricingGraph(
    sitesSource: SitesSource,
    siteId: String,
    modifier: Modifier = Modifier,
    display: DisplayBehavior = DisplayBehavior.WHEN_SOURCE_SET,
    colors: GraphColors = GraphColorDefaults.colors(),
    onError: ((String) -> Unit)? = null,
    onRefreshSuccess: (() -> Unit)? = null,
    minYAxisPrice: Double? = null,
    gridLineDotSize: Float = 4f
) {
    val context = LocalContext.current

    val pricingGraphSource = rememberPricingGraphSource(sitesSource)

    val state by pricingGraphSource.state.collectAsStateWithLifecycle()

    // Handle effects
    LaunchedEffect(pricingGraphSource) {
        pricingGraphSource.effects.collectLatest { effect ->
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
        pricingGraphSource.loadPricing(siteId)
    }

    ElvahChargeTheme(
        darkTheme = shouldUseDarkColors(sitesSource.config.darkTheme),
        customLightColorScheme = sitesSource.config.customLightColorScheme,
        customDarkColorScheme = sitesSource.config.customDarkColorScheme,
    ) {
        when (val currentState = state) {
            is PricingGraphState.Loading -> {
                DisplayContent(displayBehaviour = display) {
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
                        context.openSite(
                            dealId = siteId,
                            sourceInstanceId = sitesSource.instanceId,
                        )
                    }
                )
            }

            is PricingGraphState.Error -> {
                DisplayContent(displayBehaviour = display) {
                    PricingGraphError(
                        onRetry = {
                            pricingGraphSource.retryLoad()
                        },
                        modifier = modifier
                    )
                }
            }

            is PricingGraphState.Empty -> {
                DisplayContent(displayBehaviour = display) {
                    PricingGraphEmpty(modifier)
                }
            }
        }
    }
}

@Preview
@Composable
private fun PricingGraphPreview() {
    PricingGraph(
        sitesSource = rememberSitesSource(),
        siteId = "testing2",
        display = DisplayBehavior.WHEN_SOURCE_SET,
    )
}
