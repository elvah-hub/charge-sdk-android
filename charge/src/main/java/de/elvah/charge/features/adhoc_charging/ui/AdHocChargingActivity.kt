package de.elvah.charge.features.adhoc_charging.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ActiveChargingRoute
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ChargingPointDetailRoute
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ChargingStartRoute
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.HelpAndSupportRoute
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ReviewRoute
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.SiteDetailRoute
import de.elvah.charge.features.adhoc_charging.ui.screens.activecharging.ActiveChargingScreen
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.ChargingPointDetailScreen
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart.ChargingStartScreen
import de.elvah.charge.features.adhoc_charging.ui.screens.help.HelpAndSupportScreen
import de.elvah.charge.features.adhoc_charging.ui.screens.review.ReviewScreen
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailScreen
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

class AdHocChargingActivity : ComponentActivity() {

    companion object {
        const val ARG_DEAL_ID = "dealId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dealId = intent.extras?.getString(ARG_DEAL_ID).orEmpty()

        setContent {
            ElvahChargeTheme(darkTheme = shouldUseDarkColors(ChargeConfig.config.darkTheme)) {
                AdHocChargingGraph(dealId) {
                    finish()
                }
            }
        }
    }
}

@Composable
internal fun AdHocChargingGraph(dealId: String, onFinishClicked: () -> Unit) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = SiteDetailRoute(dealId)) {

        composable<SiteDetailRoute> {
            SiteDetailScreen(koinViewModel()) { evseId, signedOffer ->
                navController.navigate(
                    ChargingPointDetailRoute(
                        dealId = dealId,
                        evseId = evseId,
                        signedOffer = signedOffer
                    )
                )
            }
        }

        composable<ChargingPointDetailRoute> {
            ChargingPointDetailScreen(koinViewModel(), onBackClick = {
                navController.navigateUp()

            }, onPaymentSuccess = { evseId, paymentId ->
                navController.navigate(ChargingStartRoute(evseId, paymentId))
            })
        }

        composable<ChargingStartRoute> {
            val paymentId = it.arguments?.getString("paymentId").orEmpty()

            ChargingStartScreen(koinViewModel()) {
                navController.navigate(ActiveChargingRoute(paymentId))
            }
        }

        composable<ActiveChargingRoute> {
            val paymentId = it.arguments?.getString("paymentId").orEmpty()

            ActiveChargingScreen(
                viewModel = koinViewModel(),
                onSupportClick = {
                    navController.navigate(HelpAndSupportRoute)
                }, onStopClick = {
                    navController.navigate(ReviewRoute(paymentId))
                }
            )
        }

        composable<HelpAndSupportRoute> {
            HelpAndSupportScreen(koinViewModel()) {
                navController.navigateUp()
            }
        }
        composable<ReviewRoute> {
            ReviewScreen(koinViewModel()) {
                onFinishClicked()
            }
        }
    }
}


internal sealed class AdHocChargingScreens {
    @Serializable
    internal data class SiteDetailRoute(
        val dealId: String,
    ) : AdHocChargingScreens()

    @Serializable
    internal data class ActiveChargingRoute(
        val paymentId: String,
    ) : AdHocChargingScreens()

    @Serializable
    internal data class ChargingStartRoute(
        val evseId: String,
        val paymentId: String,
    ) : AdHocChargingScreens()

    @Serializable
    internal data class ChargingPointDetailRoute(
        val dealId: String,
        val evseId: String,
        val signedOffer: String,
    ) : AdHocChargingScreens()

    @Serializable
    data object HelpAndSupportRoute : AdHocChargingScreens()

    @Serializable
    internal data class ReviewRoute(
        val paymentId: String,
    ) : AdHocChargingScreens()
}

