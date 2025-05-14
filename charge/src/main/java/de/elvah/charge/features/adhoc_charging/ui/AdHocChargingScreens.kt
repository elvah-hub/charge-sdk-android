package de.elvah.charge.features.adhoc_charging.ui

import kotlinx.serialization.Serializable

internal sealed class AdHocChargingScreens {
    val baseRoute = "https://www.elvah.de/"

    @Serializable
    data class SiteDetailRoute(
        val dealId: String,
    ) : AdHocChargingScreens()

    @Serializable
    data object ActiveChargingRoute : AdHocChargingScreens(), Deeplink {
        override val route: String
            get() = baseRoute + "activeCharging"
    }

    @Serializable
    data class ChargingStartRoute(
        val evseId: String,
        val paymentId: String,
    ) : AdHocChargingScreens()

    @Serializable
    data class ChargingPointDetailRoute(
        val dealId: String,
        val evseId: String,
        val signedOffer: String,
    ) : AdHocChargingScreens()

    @Serializable
    data object HelpAndSupportRoute : AdHocChargingScreens()

    @Serializable
    data object ReviewRoute : AdHocChargingScreens()
}

internal interface Deeplink {
    val route: String
}