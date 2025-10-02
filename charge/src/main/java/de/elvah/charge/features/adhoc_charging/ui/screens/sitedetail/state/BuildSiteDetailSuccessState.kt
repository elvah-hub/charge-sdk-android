package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.state

import android.content.Context
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.ChargePointItemUI
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailState
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.getChargePointAvailabilityStatusTextResId
import de.elvah.charge.features.sites.domain.extension.getSlotAtTime
import de.elvah.charge.features.sites.domain.extension.toUI
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.extension.formatKW
import de.elvah.charge.features.sites.extension.formatted
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI

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
        val timeSlotUI = pricing.dailyPricing.today.timeSlots.getSlotAtTime()?.toUI()

        val discountExpiresAt = timeSlotUI
            ?.takeIf { it.isDiscounted }
            ?.to

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
            discountExpiresAt = discountExpiresAt,
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
}
