package de.elvah.charge.public_api.banner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.components.banner.rememberBannerSource
import de.elvah.charge.components.common.DisplayContent
import de.elvah.charge.features.sites.ui.SitesState
import de.elvah.charge.features.sites.ui.components.ChargeBanner_ActiveSession
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Content
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Empty
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Error
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Loading
import de.elvah.charge.features.sites.ui.utils.goToChargingSession
import de.elvah.charge.features.sites.ui.utils.openSite
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import de.elvah.charge.public_api.DisplayBehavior
import de.elvah.charge.public_api.sitessource.SitesSource
import de.elvah.charge.public_api.sitessource.rememberSitesSource

@Composable
public fun ChargeBanner(
    sitesSource: SitesSource,
    modifier: Modifier = Modifier,
    display: DisplayBehavior = DisplayBehavior.WHEN_SOURCE_SET,
    variant: BannerVariant = BannerVariant.DEFAULT,
) {
    val context = LocalContext.current

    val bannerSource = rememberBannerSource(sitesSource)

    val state by bannerSource.state.collectAsStateWithLifecycle()

    ElvahChargeTheme(
        darkTheme = shouldUseDarkColors(sitesSource.config.darkTheme),
        customLightColorScheme = sitesSource.config.customLightColorScheme,
        customDarkColorScheme = sitesSource.config.customDarkColorScheme,
    ) {
        when (val state = state) {
            SitesState.Idle -> {
                DisplayContent(displayBehaviour = display) {
                    ChargeBanner_Empty()
                }
            }

            SitesState.Error -> {
                DisplayContent(displayBehaviour = display) {
                    ChargeBanner_Error(modifier)
                }
            }

            SitesState.Loading -> {
                DisplayContent(displayBehaviour = display) {
                    ChargeBanner_Loading(modifier)
                }
            }

            is SitesState.Success -> {
                ChargeBanner_Content(
                    site = state.site,
                    compact = variant == BannerVariant.COMPACT,
                    modifier = modifier,
                    onDealClick = {
                        context.openSite(
                            dealId = it.id,
                            sourceInstanceId = sitesSource.instanceId,
                        )
                    },
                )
            }

            is SitesState.ActiveSession -> {
                ChargeBanner_ActiveSession(
                    site = state.site,
                    modifier = modifier,
                    navigateToSession = { context.goToChargingSession(false) },
                    navigateToSummary = { context.goToChargingSession(true) },
                )
            }

            SitesState.Empty -> {
                DisplayContent(displayBehaviour = display) {
                    ChargeBanner_Empty()
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChargeBannerPreview() {
    ChargeBanner(
        sitesSource = rememberSitesSource(),
    )
}

@Preview
@Composable
private fun CompactVariantPreview() {
    ChargeBanner(
        sitesSource = rememberSitesSource(),
        variant = BannerVariant.COMPACT,
    )
}
