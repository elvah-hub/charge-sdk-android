package de.elvah.charge.features.sites.ui.utils

import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.ui.components.ChargeBannerActiveSessionRender
import de.elvah.charge.features.sites.ui.model.AddressUI
import de.elvah.charge.features.sites.ui.model.ChargeBannerRender
import de.elvah.charge.features.sites.ui.model.ChargePointUI
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI
import de.elvah.charge.features.sites.ui.model.Location
import de.elvah.charge.platform.ui.components.graph.line.DailyPricingData
import de.elvah.charge.platform.ui.components.graph.line.PriceOffer
import de.elvah.charge.platform.ui.components.graph.line.TimeRange
import de.elvah.charge.public_api.banner.EvseId
import java.time.LocalDate
import java.time.LocalTime
import kotlin.time.Duration

internal object MockData {
    val chargePoints = List(10) {
        ChargePointUI(
            evseId = EvseId("DE*KDL*E0000049$it"),
            shortenedEvseId = "DE*$it",
            availability = ChargePointAvailability.AVAILABLE,
            standardPricePerKwh = Price(0.42, "EUR"),
            maxPowerInKW = if (it % 2 == 0) 22.0f else 300.0f,
        )
    }

    val addressUIMock = AddressUI(
        streetAddress = listOf("Köpenicker Straße 145"),
        postalCode = "12683",
        locality = "Berlin"
    )

    val siteUI = ChargeSiteUI(
        id = "1",
        cpoName = "Lidl Köpenicker Straße",
        address = addressUIMock,
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

    fun generateThreeDayPricingData(): List<DailyPricingData> {
        val today = LocalDate.now()
        return listOf(
            DailyPricingData(
                date = today.minusDays(1),
                regularPrice = 0.25,
                offers = listOf(
                    PriceOffer(
                        TimeRange(LocalTime.of(2, 0), LocalTime.of(6, 0)),
                        0.15
                    ),
                    PriceOffer(
                        TimeRange(LocalTime.of(14, 30), LocalTime.of(16, 0)),
                        0.18
                    )
                )
            ),
            DailyPricingData(
                date = today,
                regularPrice = 0.28,
                offers = listOf(
                    PriceOffer(
                        TimeRange(LocalTime.of(1, 0), LocalTime.of(5, 0)),
                        0.12
                    ),
                    PriceOffer(
                        TimeRange(LocalTime.of(13, 0), LocalTime.of(15, 30)),
                        0.20
                    )
                )
            ),
            DailyPricingData(
                date = today.plusDays(1),
                regularPrice = 0.26,
                offers = listOf(
                    PriceOffer(
                        TimeRange(LocalTime.of(3, 30), LocalTime.of(7, 0)),
                        0.16
                    ),
                    PriceOffer(
                        TimeRange(LocalTime.of(12, 0), LocalTime.of(14, 30)),
                        0.19
                    )
                )
            )
        )
    }

}
