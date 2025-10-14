package de.elvah.charge.public_api.banner

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingActivity
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ActiveChargingRoute
import de.elvah.charge.features.sites.ui.SitesState
import de.elvah.charge.features.sites.ui.SitesViewModel
import de.elvah.charge.features.sites.ui.components.ChargeBanner_ActiveSession
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Content
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Empty
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Error
import de.elvah.charge.features.sites.ui.components.ChargeBanner_Loading
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import de.elvah.charge.public_api.DisplayBehavior
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject


@Composable
public fun ChargeBanner(
    modifier: Modifier = Modifier,
    display: DisplayBehavior = DisplayBehavior.WHEN_SOURCE_SET,
    variant: BannerVariant = BannerVariant.DEFAULT,
) {
    val sitesViewModel: SitesViewModel = koinViewModel()
    val config: Config = koinInject()

    val state by sitesViewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    ElvahChargeTheme(darkTheme = shouldUseDarkColors(config.darkTheme)) {
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


internal fun Context.openSite(dealId: String) {
    val intent =
        Intent(this, AdHocChargingActivity::class.java).apply {
            putExtra(AdHocChargingActivity.ARG_SITE_ID, dealId)
        }
    startActivity(intent)
}


internal fun Context.goToChargingSession() {
    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        ActiveChargingRoute.route.toUri(),
        this,
        AdHocChargingActivity::class.java
    )

    val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
        addNextIntentWithParentStack(deepLinkIntent)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    deepLinkPendingIntent?.send()
}

