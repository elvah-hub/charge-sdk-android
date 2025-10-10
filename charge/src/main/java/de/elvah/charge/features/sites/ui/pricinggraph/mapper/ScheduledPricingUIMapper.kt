package de.elvah.charge.features.sites.ui.pricinggraph.mapper

import de.elvah.charge.features.sites.domain.extension.timeSlotToLocalDateTime
import de.elvah.charge.features.sites.domain.model.BlockingFee
import de.elvah.charge.features.sites.domain.model.ChargeSite.ChargePoint.Offer.Price
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.ui.pricinggraph.model.ScheduledPricingUI
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal fun ScheduledPricing.toUI(): ScheduledPricingUI = ScheduledPricingUI(
    dailyPricing = dailyPricing.toUI(),
    standardPrice = standardPrice.toUI()
)

internal fun ScheduledPricing.DailyPricing.toUI(): ScheduledPricingUI.DailyPricingUI =
    ScheduledPricingUI.DailyPricingUI(
        yesterday = yesterday.toUI(),
        today = today.toUI(),
        tomorrow = tomorrow.toUI()
    )

internal fun ScheduledPricing.Day.toUI(): ScheduledPricingUI.DayUI = ScheduledPricingUI.DayUI(
    lowestPrice = lowestPrice.toUI(),
    trend = trend,
    timeSlots = timeSlots.map { it.toUI() }
)

@OptIn(ExperimentalTime::class)
internal fun ScheduledPricing.TimeSlot.toUI(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    defaultTime: LocalDateTime = Clock.System.now().toLocalDateTime(timeZone),
): ScheduledPricingUI.TimeSlotUI {
    return ScheduledPricingUI.TimeSlotUI(
        isDiscounted = isDiscounted,
        price = price.toUI(),
        from = from.timeSlotToLocalDateTime() ?: defaultTime,
        fromText = from,
        to = to.timeSlotToLocalDateTime() ?: defaultTime,
        toText = to,
    )
}

internal fun Price.toUI(): ScheduledPricingUI.PriceUI = ScheduledPricingUI.PriceUI(
    energyPricePerKWh = energyPricePerKWh,
    baseFee = baseFee,
    blockingFee = blockingFee?.toUI(),
    currency = currency,
)

internal fun BlockingFee.toUI(): ScheduledPricingUI.PriceUI.BlockingFeeUI =
    ScheduledPricingUI.PriceUI.BlockingFeeUI(
        pricePerMinute = pricePerMinute,
        startsAfterMinutes = startsAfterMinutes,
        maxAmount = maxAmount,
        timeSlots = timeSlots,
        currency = currency,
    )
