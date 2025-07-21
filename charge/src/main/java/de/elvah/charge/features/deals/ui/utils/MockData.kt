package de.elvah.charge.features.deals.ui.utils

import de.elvah.charge.features.deals.ui.model.ChargePointUI
import de.elvah.charge.features.deals.ui.model.DealUI
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI

internal object MockData {
    val chargePoints = List(10) {
        ChargePointUI(
            "DE*KDL*E0000049$it",
            0.42,
            if (it % 2 == 0) "AC" else "DC",
            if (it % 2 == 0) 22 else 300,
        )
    }

    val dealUI = DealUI(
        id = "1",
        cpoName = "Lidl Köpenicker Straße",
        address = "Köpenicker Straße 145 12683 Berlin",
        lat = 6.7,
        lng = 8.9,
        pricePerKw = 0.42,
        campaignEnd = "2025-07-23T10:21:28.405Z",
        chargePoints = chargePoints
    )

    val siteUI = ChargeSiteUI(
        id = "1",
        cpoName = "Lidl Köpenicker Straße",
        address = "Köpenicker Straße 145 12683 Berlin",
        lat = 6.7,
        lng = 8.9,
        pricePerKw = 0.42,
        campaignEnd = "2025-07-23T10:21:28.405Z",
        chargePoints = chargePoints
    )

    val dealWithoutChargePoints = dealUI.copy(chargePoints = emptyList())
    val siteWithoutChargePoints = siteUI.copy(chargePoints = emptyList())
}
