package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.state

import android.content.Context
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailState
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.formatKW
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.getChargePointAvailabilityStatusTextResId
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.extension.formatted
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI

internal class BuildSiteDetailSuccessState(
    private val context: Context
) {

    operator fun invoke(
        searchInput: String,
        ui: ChargeSiteUI
    ): SiteDetailState.Success {
        val ids = ui.chargePoints
            .map { it.shortenedEvseId }

        val (common, unique) = getCommonAndUniquePrefixes(ids)

        val chargePoints = ui.chargePoints
            .mapIndexed { index, cp ->
                val shortenedEvseId = unique.getOrNull(index)
                    ?.takeIf { it.isNotBlank() }
                // if there are no unique strings, means all elements have the same common text
                    ?: common

                val isFiltered = isChargePointFiltered(
                    searchInput = searchInput,
                    evseId = cp.shortenedEvseId,
                    availability = cp.availability,
                    pricePerKwh = cp.pricePerKwh,
                    energyValue = cp.energyValue,
                )

                val updatedChargePoint = cp.copy(
                    shortenedEvseId = shortenedEvseId,
                )

                Pair(
                    updatedChargePoint,
                    isFiltered,
                )

            }
            .sortedBy { (cp, _) -> cp.shortenedEvseId }
            .filter { (_, isFiltered) -> isFiltered }
            .map { (cp, _) -> cp }

        return SiteDetailState.Success(
            searchInput = searchInput,
            chargeSiteUI = ui.copy(
                chargePoints = chargePoints,
            ),
        )
    }

    private fun getCommonAndUniquePrefixes(
        ids: List<String>,
    ): Pair<String, List<String>> {
        val commonPrefix = ids
            .takeIf { it.isNotEmpty() }
            ?.reduce { accumulator, nextElement ->
                accumulator.commonPrefixWith(nextElement, true)
            }
            ?: ""

        val uniqueStringList = ids.map { it.removePrefix(commonPrefix) }

        return commonPrefix to uniqueStringList
    }

    private fun isChargePointFiltered(
        searchInput: String,
        evseId: String,
        availability: ChargePointAvailability,
        pricePerKwh: Price,
        energyValue: Float?,
    ): Boolean {
        // always include results if search input is empty
        if (searchInput.isBlank()) return true

        val availability = getChargePointAvailabilityStatusTextResId(
            availability = availability,
        ).let { stringResId -> context.getString(stringResId) }

        val price = pricePerKwh.formatted()

        val maxPowerInKw = energyValue?.formatKW()

        val wordsToCheck = listOfNotNull(
            evseId,
            availability,
            price,
            maxPowerInKw,
        )

        return wordsToCheck.any { word -> word.contains(searchInput, ignoreCase = true) }
    }
}
