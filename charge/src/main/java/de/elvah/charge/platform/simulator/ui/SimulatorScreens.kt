package de.elvah.charge.platform.simulator.ui

import kotlinx.serialization.Serializable

internal sealed class SimulatorScreens {

    @Serializable
    data object ConfigListRoute : SimulatorScreens()

    @Serializable
    data object ActiveChargingRoute : SimulatorScreens(), Deeplink {
        override val route: String
            get() = baseRoute + "activeCharging"
    }

    @Serializable
    data class ChargingStartRoute(
        val evseId: String,
        val paymentId: String,
    ) : SimulatorScreens()

    @Serializable
    data class ChargingPointDetailRoute(
        val siteId: String,
        val evseId: String,
    ) : SimulatorScreens(), Deeplink {
        override val route: String
            get() = ROUTE

        companion object {
            const val ROUTE = baseRoute + "chargingPointDetail"
        }
    }

    @Serializable
    data object HelpAndSupportRoute : SimulatorScreens()

    @Serializable
    data object ReviewRoute : SimulatorScreens()
}

internal interface Deeplink {
    val route: String
}

private const val baseRoute = "https://www.elvah.de/"
