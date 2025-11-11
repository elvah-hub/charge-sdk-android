package de.elvah.charge.components.pricinggraph

import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Pricing
import de.elvah.charge.features.sites.ui.model.AddressUI
import de.elvah.charge.features.sites.ui.model.ChargePointUI
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI
import de.elvah.charge.features.sites.ui.pricinggraph.model.ScheduledPricingUI
import de.elvah.charge.public_api.model.EvseId
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal object PricingGraphComponentSourcePreviewMock {

    @OptIn(ExperimentalTime::class)
    val scheduledPricingMock = ScheduledPricingUI(
        dailyPricing = ScheduledPricingUI.DailyPricingUI(
            yesterday = ScheduledPricingUI.DayUI(
                lowestPrice = ScheduledPricingUI.PriceUI(
                    energyPricePerKWh = Pricing(0.22, "EUR"),
                    baseFee = Pricing(0.10, "€"),
                    blockingFee = null,
                    currency = "EUR",
                ),
                trend = "down",
                timeSlots = listOf(
                    ScheduledPricingUI.TimeSlotUI(
                        isDiscounted = false,
                        price = ScheduledPricingUI.PriceUI(
                            energyPricePerKWh = Pricing(0.25, "EUR"),
                            baseFee = Pricing(0.10, "€"),
                            blockingFee = null,
                            currency = "EUR"
                        ),
                        from = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        fromText = "08:00",
                        to = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        toText = "10:00"
                    ),
                    ScheduledPricingUI.TimeSlotUI(
                        isDiscounted = true,
                        price = ScheduledPricingUI.PriceUI(
                            energyPricePerKWh = Pricing(0.18, "EUR"),
                            baseFee = Pricing(0.05, "€"),
                            blockingFee = ScheduledPricingUI.PriceUI.BlockingFeeUI(
                                pricePerMinute = Pricing(0.02, "€/min"),
                                startsAfterMinutes = 60,
                                maxAmount = Pricing(5.0, "€"),
                                timeSlots = null,
                                currency = "EUR"
                            ),
                            currency = "EUR"
                        ),
                        from = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        fromText = "10:00",
                        to = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        toText = "12:00"
                    )
                )
            ),
            today = ScheduledPricingUI.DayUI(
                lowestPrice = ScheduledPricingUI.PriceUI(
                    energyPricePerKWh = Pricing(0.20, "EUR"),
                    baseFee = Pricing(0.05, "€"),
                    blockingFee = null,
                    currency = "EUR"
                ),
                trend = "stable",
                timeSlots = listOf(
                    ScheduledPricingUI.TimeSlotUI(
                        isDiscounted = true,
                        price = ScheduledPricingUI.PriceUI(
                            energyPricePerKWh = Pricing(0.19, "EUR"),
                            baseFee = Pricing(0.04, "€"),
                            blockingFee = null,
                            currency = "EUR"
                        ),
                        from = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        fromText = "08:00",
                        to = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        toText = "10:00"
                    ),
                    ScheduledPricingUI.TimeSlotUI(
                        isDiscounted = false,
                        price = ScheduledPricingUI.PriceUI(
                            energyPricePerKWh = Pricing(0.23, "EUR"),
                            baseFee = Pricing(0.06, "€"),
                            blockingFee = ScheduledPricingUI.PriceUI.BlockingFeeUI(
                                pricePerMinute = Pricing(0.03, "€/min"),
                                startsAfterMinutes = 45,
                                maxAmount = Pricing(4.0, "€"),
                                timeSlots = null,
                                currency = "EUR"
                            ),
                            currency = "EUR"
                        ),
                        from = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        fromText = "10:00",
                        to = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        toText = "12:00"
                    )
                )
            ),
            tomorrow = ScheduledPricingUI.DayUI(
                lowestPrice = ScheduledPricingUI.PriceUI(
                    energyPricePerKWh = Pricing(0.17, "EUR"),
                    baseFee = Pricing(0.04, "€"),
                    blockingFee = null,
                    currency = "EUR"
                ),
                trend = "up",
                timeSlots = listOf(
                    ScheduledPricingUI.TimeSlotUI(
                        isDiscounted = false,
                        price = ScheduledPricingUI.PriceUI(
                            energyPricePerKWh = Pricing(0.22, "EUR"),
                            baseFee = Pricing(0.05, "€"),
                            blockingFee = null,
                            currency = "EUR"
                        ),
                        from = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        fromText = "08:00",
                        to = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        toText = "10:00"
                    ),
                    ScheduledPricingUI.TimeSlotUI(
                        isDiscounted = true,
                        price = ScheduledPricingUI.PriceUI(
                            energyPricePerKWh = Pricing(0.16, "EUR"),
                            baseFee = Pricing(0.04, "€"),
                            blockingFee = null,
                            currency = "EUR"
                        ),
                        from = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        fromText = "10:00",
                        to = Clock.System.now()
                            .toLocalDateTime(TimeZone.Companion.currentSystemDefault()),
                        toText = "12:00"
                    )
                )
            )
        ),
        standardPrice = ScheduledPricingUI.PriceUI(
            energyPricePerKWh = Pricing(0.21, "EUR"),
            baseFee = Pricing(0.05, "€"),
            blockingFee = null,
            currency = "EUR"
        )
    )

    val siteDetailMock = ChargeSiteUI(
        id = "testing",
        cpoName = "CPO: Testing GmbH",
        address = AddressUI(
            listOf(
                "local address 1 Germany"
            ),
            "0000",
            "Germany"
        ),
        lat = 0.0,
        lng = 0.0,
        campaignEnd = "2025-04-23T10:21:28.405000000Z",
        pricePerKw = 0.24,
        chargePoints = listOf(
            ChargePointUI(
                evseId = EvseId("evse1"),
                shortenedEvseId = "e1",
                availability = ChargePointAvailability.AVAILABLE,
                standardPricePerKwh = Pricing(0.5, "EUR"),
                maxPowerInKW = 22f,
                powerType = "DC",
            ),
        )
    )
}
