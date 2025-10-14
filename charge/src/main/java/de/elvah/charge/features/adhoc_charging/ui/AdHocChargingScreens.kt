package de.elvah.charge.features.adhoc_charging.ui

import kotlinx.serialization.Serializable

internal sealed class AdHocChargingScreens {

    @Serializable
    data class SiteDetailRoute(
        val siteId: String,
    ) : AdHocChargingScreens()

    @Serializable
    data object ActiveChargingRoute : AdHocChargingScreens(), Deeplink {
        override val route: String
            get() = baseRoute + "activeCharging"
    }

    @Serializable
    data class ChargingStartRoute(
        val shortenedEvseId: String,
        val paymentId: String,
    ) : AdHocChargingScreens()

    @Serializable
    data class ChargingPointDetailRoute(
        val siteId: String,
        val evseId: String,
    ) : AdHocChargingScreens(), Deeplink {
        override val route: String
            get() = ROUTE

        companion object {
            const val ROUTE = baseRoute + "chargingPointDetail"
        }
    }

    @Serializable
    data object HelpAndSupportRoute : AdHocChargingScreens()

    @Serializable
    data object ReviewRoute : AdHocChargingScreens()
}

internal interface Deeplink {
    val route: String
}

private const val baseRoute = "https://www.elvah.de/"
