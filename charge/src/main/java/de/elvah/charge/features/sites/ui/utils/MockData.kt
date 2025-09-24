package de.elvah.charge.features.sites.ui.utils

import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.ChargeSite
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
            if (it % 2 == 0) 22.0f else 300.0f,
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

    val chargeSites = List(3) { siteIndex ->
        ChargeSite(
            address = ChargeSite.Address(
                streetAddress = listOf("Address $siteIndex"),
                postalCode = "curae",
                locality = "gravida"
            ),
            evses = List(10) { evseIndex ->
                ChargeSite.ChargePoint(
                    evseId = "DE*KDL*E000004$siteIndex$evseIndex",
                    offer = ChargeSite.ChargePoint.Offer(
                        price = ChargeSite.ChargePoint.Offer.Price(
                            energyPricePerKWh = 22.23,
                            baseFee = 2847,
                            currency = "nostra",
                            blockingFee = null
                        ),
                        type = "fusce",
                        expiresAt = "posuere",
                        originalPrice = null,
                        campaignEndsAt = "rhoncus",
                        signedOffer = "iusto"
                    ),
                    powerSpecification = ChargeSite.PowerSpecification(
                        maxPowerInKW = 22.0f,
                        type = if (evseIndex % 2 == 0) "AC" else "DC"
                    ),
                    availability = ChargePointAvailability.AVAILABLE,
                    normalizedEvseId = "DEKDLE000004$siteIndex$evseIndex",
                )
            },
            location = listOf(6.7, 8.9),
            id = "odio",
            operatorName = "Robby Stafford",
            prevalentPowerType = "felis",
        )
    }
}
