package de.elvah.charge.public_api.banner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.features.sites.ui.SitesState
import de.elvah.charge.features.sites.ui.SitesViewModel
import de.elvah.charge.features.sites.ui.components.ChargeBanner_ActiveSession
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Content
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Empty
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Error
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Loading
import de.elvah.charge.features.sites.ui.utils.goToChargingSession
import de.elvah.charge.features.sites.ui.utils.openSite
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import de.elvah.charge.public_api.DisplayBehavior
import de.elvah.charge.platform.di.sdkGet
import de.elvah.charge.platform.di.sdkViewModel


@Composable
public fun ChargeBanner(
    modifier: Modifier = Modifier,
    display: DisplayBehavior = DisplayBehavior.WHEN_SOURCE_SET,
    variant: BannerVariant = BannerVariant.DEFAULT,
) {
    val sitesViewModel: SitesViewModel = sdkViewModel()
    val config: Config = sdkGet()

    val state by sitesViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ElvahChargeTheme(
        darkTheme = shouldUseDarkColors(config.darkTheme),
        customLightColorScheme = config.customLightColorScheme,
        customDarkColorScheme = config.customDarkColorScheme
    ) {
        when (val state = state) {
            SitesState.Error -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    ChargeBanner_Error(modifier)
                }
            }

            SitesState.Loading -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    ChargeBanner_Loading(modifier)
                }
            }

            is SitesState.Success -> ChargeBanner_Content(
                site = state.site,
                compact = variant == BannerVariant.COMPACT,
                modifier = modifier
            ) {
                context.openSite(dealId = it.id)
            }

            is SitesState.ActiveSession -> ChargeBanner_ActiveSession(
                site = state.site,
                modifier = modifier,
                onBannerClick = {
                    context.goToChargingSession()
                }
            )

            SitesState.Empty -> ChargeBanner_Empty()
        }
    }
}
