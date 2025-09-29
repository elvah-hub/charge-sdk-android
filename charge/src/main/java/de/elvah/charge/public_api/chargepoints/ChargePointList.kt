package de.elvah.charge.public_api.chargepoints

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.public_api.DisplayBehavior
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingActivity
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ChargingPointDetailRoute
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailState
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailViewModel
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.ChargePointsList
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
public fun ChargePointList(
    modifier: Modifier = Modifier,
    display: DisplayBehavior = DisplayBehavior.WHEN_SOURCE_SET,
) {
    val siteDetailViewModel: SiteDetailViewModel = koinViewModel()
    val config: Config = koinInject()

    val state by siteDetailViewModel.state.collectAsStateWithLifecycle()

    ElvahChargeTheme(darkTheme = shouldUseDarkColors(config.darkTheme)) {
        when (val state = state) {
            SiteDetailState.Error -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {

                }
            }

            SiteDetailState.Loading -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {

                }
            }

            is SiteDetailState.Success -> {
                ChargePointsList(state.chargeSiteUI.chargePoints, onItemClick = { evseId -> })
            }
        }
    }
}

internal fun Context.goToChargePoint(evseId: String) {
    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        ChargingPointDetailRoute(
            siteId = "",
            evseId = evseId,
        ).route.toUri(),
        this,
        AdHocChargingActivity::class.java
    )

    val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
        addNextIntentWithParentStack(deepLinkIntent)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    deepLinkPendingIntent?.send()
}
