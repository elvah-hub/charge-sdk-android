package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.state

import android.content.Context
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.ChargePointItemUI
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailState
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.getChargePointAvailabilityStatusTextResId
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.Pricing
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.extension.formatKW
import de.elvah.charge.features.sites.extension.formatted
import de.elvah.charge.features.sites.ui.mapper.toUI
import de.elvah.charge.features.sites.ui.pricinggraph.mapper.toUI

internal class BuildSiteDetailSuccessState(
    private val context: Context
) {

    operator fun invoke(
        chargeSite: ChargeSite,
        pricing: ScheduledPricing,
        timeSlot: ScheduledPricing.TimeSlot?,
        searchInput: String,
        address: String?,
    ): SiteDetailState.Success {
        val timeSlotUI = timeSlot?.toUI()

        val discountExpiresAt = timeSlotUI
            ?.takeIf { it.isDiscounted }
            ?.to

        val standardPricePerKWh = pricing.standardPrice.energyPricePerKWh

        /*
        Pricing-Schedule returns the prices for the prevalent power type of the given site. We will
        assign this prices to the corresponding charge point based on its power type.
        Remaining charge points will depend on the original response to the sites offer, but will
        update its price (when the discount time is expired) based on the information in the time slot
        table (provided from pricing-schedule).
        */
        val allChargePoints = chargeSite.toUI()
            .chargePoints
            .map { cpUI ->
                if (cpUI.powerType != chargeSite.prevalentPowerType) {
                    return@map ChargePointItemUI(
                        evseId = cpUI.evseId.value,
                        shortenedEvseId = cpUI.shortenedEvseId,
                        availability = cpUI.availability,
                        standardPricePerKwh = standardPricePerKWh,
                        todayPricePerKwh = standardPricePerKWh,
                        maxPowerInKW = cpUI.maxPowerInKW,
                        powerType = cpUI.powerType,
                        hasDiscount = false,
                    )
                }

                val todayPricePerKwh = timeSlotUI?.price?.energyPricePerKWh
                    ?: standardPricePerKWh

                ChargePointItemUI(
                    evseId = cpUI.evseId.value,
                    shortenedEvseId = cpUI.shortenedEvseId,
                    availability = cpUI.availability,
                    standardPricePerKwh = standardPricePerKWh,
                    todayPricePerKwh = todayPricePerKwh,
                    maxPowerInKW = cpUI.maxPowerInKW,
                    powerType = cpUI.powerType,
                    hasDiscount = timeSlotUI?.isDiscounted == true,
                )
            }

        val filteredChargePoints = allChargePoints
            .map { itemUI ->
                val isFiltered = isChargePointFiltered(
                    searchInput = searchInput,
                    evseId = itemUI.shortenedEvseId,
                    availability = itemUI.availability,
                    pricePerKwh = itemUI.standardPricePerKwh,
                    todayPricePerKwh = itemUI.todayPricePerKwh,
                    powerType = itemUI.powerType,
                    maxPowerInKW = itemUI.maxPowerInKW,
                )

                Pair(
                    itemUI,
                    isFiltered,
                )
            }
            .sortedBy { (cp, _) -> cp.shortenedEvseId }
            .filter { (_, isFiltered) -> isFiltered }
            .map { (cp, _) -> cp }

        return SiteDetailState.Success(
            discountExpiresAt = discountExpiresAt,
            operatorName = chargeSite.operatorName,
            address = address,
            coordinates = Pair(chargeSite.location.first(), chargeSite.location.last()),
            searchInput = searchInput,
            chargePoints = filteredChargePoints,
            noSearchResults = filteredChargePoints.isEmpty(),
            noStations = allChargePoints.isEmpty(),
        )
    }

    private fun isChargePointFiltered(
        searchInput: String,
        evseId: String,
        availability: ChargePointAvailability,
        pricePerKwh: Pricing,
        todayPricePerKwh: Pricing,
        powerType: String?,
        maxPowerInKW: Float?,
    ): Boolean {
        // always include results if search input is empty
        if (searchInput.isBlank()) return true

        val availability = getChargePointAvailabilityStatusTextResId(
            availability = availability,
        ).let { stringResId -> context.getString(stringResId) }

        val price = pricePerKwh.formatted()
        val todayPrice = todayPricePerKwh.formatted()

        val maxPowerInKw = maxPowerInKW?.formatKW()

        val wordsToCheck = listOfNotNull(
            evseId,
            availability,
            price,
            todayPrice,
            powerType,
            maxPowerInKw,
        )

        return wordsToCheck.any { word -> word.contains(searchInput, ignoreCase = true) }
    }
}
