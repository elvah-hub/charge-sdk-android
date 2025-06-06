package de.elvah.charge

import android.content.Context
import android.content.Intent
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingActivity
import de.elvah.charge.features.deals.ui.DealsState
import de.elvah.charge.features.deals.ui.DealsViewModel
import de.elvah.charge.features.deals.ui.components.DealBanner_ActiveSession
import de.elvah.charge.features.deals.ui.components.DealBanner_Content
import de.elvah.charge.features.deals.ui.components.DealBanner_Error
import de.elvah.charge.features.deals.ui.components.DealBanner_Loading
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import org.koin.androidx.compose.koinViewModel

@Composable
fun CampaignBanner(
    modifier: Modifier = Modifier,
    display: DisplayBehavior = DisplayBehavior.WHEN_SOURCE_SET,
    variant: BannerVariant = BannerVariant.DEFAULT
) {
    val dealsViewModel: DealsViewModel = koinViewModel()

    val state by dealsViewModel.state.collectAsState()
    val context = LocalContext.current

    ElvahChargeTheme(darkTheme = shouldUseDarkColors(ChargeConfig.config.darkTheme)) {
        when (state) {
            DealsState.Error -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    DealBanner_Error(modifier)
                }
            }

            DealsState.Loading -> {
                if (display != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
                    Card(modifier = modifier) {
                        DealBanner_Loading(modifier)
                    }
                }
            }

            is DealsState.Success -> DealBanner_Content(
                deal = (state as DealsState.Success).deal,
                compact = variant == BannerVariant.COMPACT,
                modifier = modifier
            ) {
                context.startCharging(dealId = it.id)
            }

            is DealsState.ActiveSession -> DealBanner_ActiveSession(
                deal = (state as DealsState.ActiveSession).deal,
                modifier = modifier,
                onBannerClick = {

                }
            )
        }
    }
}


fun Context.startCharging(dealId: String) {
    val intent =
        Intent(this, AdHocChargingActivity::class.java).apply {
            putExtra(AdHocChargingActivity.ARG_DEAL_ID, dealId)
        }
    startActivity(intent)
}


