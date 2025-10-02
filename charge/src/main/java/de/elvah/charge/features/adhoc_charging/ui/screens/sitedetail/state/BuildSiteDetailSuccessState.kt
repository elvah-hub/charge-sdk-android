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
import de.elvah.charge.features.sites.ui.mapper.toUI

internal class BuildSiteDetailSuccessState(
    private val context: Context
) {

    operator fun invoke(
        chargeSite: ChargeSite,
        pricing: ScheduledPricing,
        searchInput: String,
        address: String?,
    ): SiteDetailState.Success {
        val timeSlotUI = pricing.dailyPricing.today.timeSlots.getSlotAtTime()?.toUI()

        val discountExpiresAt = timeSlotUI
            ?.takeIf { it.isDiscounted }
            ?.to

        val standardPrice = pricing.standardPrice.let {
            Price(
                value = it.energyPricePerKWh,
                currency = it.currency,
            )
        }

        /*
        Pricing-Schedule returns the prices for the prevalent power type of the given site. We will
         assign this prices to the corresponding charge point based on its power type.
        Remaining charge points will depend on the original response to the sites offer, but will
           update its price (when the discount time is expired) based on a check to the time slot
           table provided from pricing-schedule.
        */
        val allChargePoints = chargeSite.toUI()
            .chargePoints
            .map { cpUI ->
                if (cpUI.powerType != chargeSite.prevalentPowerType) {
                    return@map ChargePointItemUI(
                        chargePointUI = cpUI,
                        standardPricePerKwh = standardPrice,
                        todayPricePerKwh = standardPrice,
                        hasDiscount = false,
                        powerType = cpUI.powerType,
                    )
                }

                val todayPricePerKwh = timeSlotUI?.let {
                    Price(
                        value = timeSlotUI.price.energyPricePerKWh,
                        currency = timeSlotUI.price.currency,
                    )
                } ?: standardPrice


                ChargePointItemUI(
                    chargePointUI = cpUI,
                    standardPricePerKwh = standardPrice,
                    todayPricePerKwh = todayPricePerKwh,
                    hasDiscount = timeSlotUI?.isDiscounted == true,
                    powerType = cpUI.powerType,
                )
            }

        val chargePoints = allChargePoints
            .map { itemUI ->
                val isFiltered = isChargePointFiltered(
                    searchInput = searchInput,
                    evseId = itemUI.chargePointUI.shortenedEvseId,
                    availability = itemUI.chargePointUI.availability,
                    pricePerKwh = itemUI.standardPricePerKwh,
                    maxPowerInKW = itemUI.chargePointUI.maxPowerInKW,
                )

                Pair(
                    itemUI,
                    isFiltered,
                )
            }
            .sortedBy { (cp, _) -> cp.chargePointUI.shortenedEvseId }
            .filter { (_, isFiltered) -> isFiltered }
            .map { (cp, _) -> cp }

        return SiteDetailState.Success(
            discountExpiresAt = discountExpiresAt,
            operatorName = chargeSite.operatorName,
            address = address,
            coordinates = Pair(chargeSite.location.first(), chargeSite.location.last()),
            searchInput = searchInput,
            chargePoints = chargePoints,
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
