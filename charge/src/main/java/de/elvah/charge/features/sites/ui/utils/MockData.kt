package de.elvah.charge.features.sites.ui.utils

import de.elvah.charge.features.sites.ui.components.ChargeBannerActiveSessionRender
import de.elvah.charge.features.sites.ui.model.ChargeBannerRender
import de.elvah.charge.features.sites.ui.model.ChargePointUI
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI
import de.elvah.charge.features.sites.ui.model.Location
import kotlin.time.Duration

internal object MockData {
    val chargePoints = List(10) {
        ChargePointUI(
            "DE*KDL*E0000049$it",
            0.42,
            if (it % 2 == 0) "AC" else "DC",
            if (it % 2 == 0) 22 else 300,
        )
    }

    val siteUI = ChargeSiteUI(
        id = "1",
        cpoName = "Lidl Köpenicker Straße",
        address = "Köpenicker Straße 145 12683 Berlin",
        lat = 6.7,
        lng = 8.9,
        pricePerKw = 0.42,
        campaignEnd = "2025-07-23T10:21:28.423423423Z",
        chargePoints = chargePoints
    )

    val chargeSiteRender = ChargeBannerRender(
        id = "id",
        cpoName = "Deal Title",
        address = "address",
        location = Location(
            lat = 0.0,
            lng = 0.0
        ),
        campaignEnd = "2025-04-23T10:21:28.405000000Z",
        originalPrice = 0.50,
        price = 0.24
    )

    val chargeSiteActiveSessionRender = ChargeBannerActiveSessionRender(
        id = "id",
        chargeTime = Duration.ZERO
    )

    val siteWithoutChargePoints = siteUI.copy(chargePoints = emptyList())
}
