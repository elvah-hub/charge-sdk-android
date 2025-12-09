package de.elvah.charge.features.adhoc_charging.ui

import de.elvah.charge.features.adhoc_charging.deeplinks.DeepLinks
import de.elvah.charge.features.adhoc_charging.deeplinks.UrlArg
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
        val finishOnBackClicked: Boolean = false,
    ) : AdHocChargingScreens() {

        companion object {
            const val ROUTE = "chargingPointDetail"

            val deepLinks = DeepLinks.getDeepLinks(
                route = ROUTE,
                args = buildArgsForUrl(isTemplate = true),
            )

            private fun buildArgsForUrl(
                isTemplate: Boolean,
                args: ChargingPointDetailRoute? = null,
            ): String {
                return DeepLinks.buildArgsForUrl(
                    args = listOf(
                        UrlArg(
                            parameterName = "siteId",
                            parameterValue = args?.siteId,
                        ),
                        UrlArg(
                            parameterName = "evseId",
                            parameterValue = args?.evseId,
                        ),
                        UrlArg(
                            parameterName = "finishOnBackClicked",
                            parameterValue = args?.finishOnBackClicked?.toString(),
                        ),
                    ),
                    isTemplate = isTemplate,
                )
            }

            fun ChargingPointDetailRoute.toDeepLinks(): List<String> = DeepLinks.getDeepLinks(
                route = ROUTE,
                args = buildArgsForUrl(isTemplate = false, args = this),
            )
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
