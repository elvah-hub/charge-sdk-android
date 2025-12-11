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
import de.elvah.charge.platform.ui.animation.slideFromBottom
import de.elvah.charge.platform.ui.animation.slideToBottom
import de.elvah.charge.platform.di.sdkViewModel

@Composable
internal fun AdHocChargingGraph(
    siteId: String,
    onFinishClicked: () -> Unit,
    onGooglePayClick: (String) -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController, startDestination = SiteDetailRoute(siteId)) {
        composable<SiteDetailRoute> {
            SiteDetailScreen(
                viewModel = sdkViewModel(),
                onCloseClick = onFinishClicked,
                onItemClick = { evseId ->
                    navController.navigate(
                        ChargingPointDetailRoute(
                            siteId = siteId,
                            evseId = evseId,
                        )
                    )
                },
            )
        }

        composable<ChargingPointDetailRoute>(
            deepLinks = listOf(
                navDeepLink<ChargingPointDetailRoute>(basePath = ChargingPointDetailRoute.ROUTE)
            ),
            enterTransition = slideFromBottom(),
            popEnterTransition = slideFromBottom(),
            exitTransition = slideToBottom(),
            popExitTransition = slideToBottom(),
        ) {
            ChargingPointDetailScreen(
                sdkViewModel(), onBackClick = {
                    navController.navigateUp()
                }, onPaymentSuccess = { shortenedEvseId, paymentId ->
                    navController.navigate(
                        ChargingStartRoute(
                            shortenedEvseId = shortenedEvseId,
                            paymentId = paymentId
                        )
                    )
                },
                onGooglePayClick = onGooglePayClick
            )
        }

        composable<ChargingStartRoute> {
            ChargingStartScreen(sdkViewModel()) {
                navController.navigate(ActiveChargingRoute)
            }
        }

        composable<ActiveChargingRoute>(
            deepLinks = listOf(
                navDeepLink<ActiveChargingRoute>(basePath = ActiveChargingRoute.route)
            )
        ) {
            ActiveChargingScreen(
                viewModel = sdkViewModel(),
                onSupportClick = {
                    navController.navigate(HelpAndSupportRoute)
                }, onStopClick = {
                    navController.navigate(ReviewRoute)
                }, onDismissClick = onFinishClicked
            )
        }

        composable<HelpAndSupportRoute> {
            HelpAndSupportScreen(sdkViewModel()) {
                navController.navigateUp()
            }
        }
        composable<ReviewRoute> {
            ReviewScreen(
                viewModel = sdkViewModel(),
                onDoneClick = onFinishClicked,
                onDismissClick = onFinishClicked,
                onContactSupport = {
                    navController.navigate(HelpAndSupportRoute)
                }
            )
        }
    }
}
