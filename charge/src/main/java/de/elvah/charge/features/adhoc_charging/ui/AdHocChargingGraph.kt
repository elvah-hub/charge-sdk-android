package de.elvah.charge.features.adhoc_charging.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
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
import org.koin.androidx.compose.koinViewModel

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

        composable<ChargingPointDetailRoute>(
            deepLinks = listOf(
                navDeepLink<ChargingPointDetailRoute>(basePath = ChargingPointDetailRoute.route)
            )
        ) {
            ChargingPointDetailScreen(koinViewModel(), onBackClick = {
                navController.navigateUp()
            }, onPaymentSuccess = { evseId, paymentId ->
                navController.navigate(ChargingStartRoute(evseId, paymentId))
            })
        }

        composable<ChargingStartRoute> {
            ChargingStartScreen(koinViewModel()) {
                navController.navigate(ActiveChargingRoute)
            }
        }

        composable<ActiveChargingRoute>(
            deepLinks = listOf(
                navDeepLink<ActiveChargingRoute>(basePath = ActiveChargingRoute.route)
            )
        ) {
            ActiveChargingScreen(
                viewModel = koinViewModel(),
                onSupportClick = {
                    navController.navigate(HelpAndSupportRoute)
                }, onStopClick = {
                    navController.navigate(ReviewRoute)
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

