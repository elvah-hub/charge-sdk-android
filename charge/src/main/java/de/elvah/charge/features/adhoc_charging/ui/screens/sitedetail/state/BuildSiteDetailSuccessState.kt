package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.state

import android.content.Context
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.ChargePointItemUI
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailState
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.getChargePointAvailabilityStatusTextResId
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.extension.formatKW
import de.elvah.charge.features.sites.extension.formatted
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI
import de.elvah.charge.features.sites.ui.utils.toLocalDateTime
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

internal class BuildSiteDetailSuccessState(
    private val context: Context
) {

    operator fun invoke(
        chargeSite: ChargeSite,
        chargeSiteUI: ChargeSiteUI,
        pricing: ScheduledPricing,
        searchInput: String,
        address: String?,
    ): SiteDetailState.Success {
        // TODO: use time slot table to determine when the discount will disappear
        val campaignExpiresAt = chargeSite.evses
            .mapNotNull { it.offer.campaignEndsAt?.toLocalDateTime() }
            .maxOrNull()

        val chargePoints = chargeSiteUI.chargePoints
            .map { cp ->
                val isFiltered = isChargePointFiltered(
                    searchInput = searchInput,
                    evseId = cp.shortenedEvseId,
                    availability = cp.availability,
                    pricePerKwh = cp.standardPricePerKwh,
                    maxPowerInKW = cp.maxPowerInKW,
                )

                Pair(
                    cp,
                    isFiltered,
                )
            }
            .sortedBy { (cp, _) -> cp.shortenedEvseId }
            .filter { (_, isFiltered) -> isFiltered }
            .map { (cp, _) -> cp }

        val pricingForChargePoints = chargePoints
            .map {
                val standardPrice = Price(
                    value = pricing.standardPrice.energyPricePerKWh,
                    currency = pricing.standardPrice.currency,
                )

                val priceAtCurrentTime = pricing.dailyPricing.today.timeSlots.getSlotAtTime()

                when {
                    priceAtCurrentTime != null -> {
                        val todayPricePerKwh = Price(
                            value = priceAtCurrentTime.price.energyPricePerKWh,
                            currency = priceAtCurrentTime.price.currency,
                        )

                        ChargePointItemUI(
                            chargePointUI = it,
                            standardPricePerKwh = it.standardPricePerKwh,
                            todayPricePerKwh = todayPricePerKwh,
                            hasDiscount = priceAtCurrentTime.isDiscounted,
                        )
                    }

                    else -> {
                        ChargePointItemUI(
                            chargePointUI = it,
                            standardPricePerKwh = standardPrice,
                            todayPricePerKwh = standardPrice,
                            hasDiscount = false,
                        )
                    }
                }
            }

        return SiteDetailState.Success(
            campaignExpireAt = campaignExpiresAt,
            operatorName = chargeSiteUI.cpoName,
            address = address,
            searchInput = searchInput,
            pricingForChargePoints = pricingForChargePoints,
            chargeSiteUI = chargeSiteUI.copy(
                chargePoints = chargePoints,
            ),
        )
    }

    private fun isChargePointFiltered(
        searchInput: String,
        evseId: String,
        availability: ChargePointAvailability,
        pricePerKwh: Price,
        maxPowerInKW: Float?,
    ): Boolean {
        // always include results if search input is empty
        if (searchInput.isBlank()) return true

        val availability = getChargePointAvailabilityStatusTextResId(
            availability = availability,
        ).let { stringResId -> context.getString(stringResId) }

        val price = pricePerKwh.formatted()

        val maxPowerInKw = maxPowerInKW?.formatKW()

        val wordsToCheck = listOfNotNull(
            evseId,
            availability,
            price,
            maxPowerInKw,
        )

        return wordsToCheck.any { word -> word.contains(searchInput, ignoreCase = true) }
    }


    fun List<ScheduledPricing.TimeSlot>.getSlotAtTime(
        now: LocalDateTime = LocalDateTime.now(),
    ): ScheduledPricing.TimeSlot? {
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        val now = now.toLocalTime()

        return find { timeSlot ->
            val fromTime = LocalTime.parse(timeSlot.from, formatter)
            val toTime = LocalTime.parse(timeSlot.to, formatter)

            if (fromTime <= toTime) {
                now in fromTime..toTime
            } else {
                now >= fromTime || now <= toTime
            }
        }
    }
}
