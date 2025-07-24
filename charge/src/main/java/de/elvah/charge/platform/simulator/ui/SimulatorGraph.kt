package de.elvah.charge.platform.simulator.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
internal fun SimulatorGraph(onFinishClicked: () -> Unit) {

    val navController = rememberNavController()

    NavHost(navController, startDestination = SimulatorScreens.ConfigListRoute) {

        composable<SimulatorScreens.ConfigListRoute> {
            /*
            SimulatorListScreen(
                simulatorListViewModel = koinViewModel<SimulatorListViewModel>(),
                onConfigClick = {

                },
                onBackClicked = onFinishClicked
            )
             */
        }
    }
}

