package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.state

import android.content.Context
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailState
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.getChargePointAvailabilityStatusTextResId
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.extension.formatKW
import de.elvah.charge.features.sites.extension.formatted
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI

internal class BuildSiteDetailSuccessState(
    private val context: Context
) {

    operator fun invoke(
        searchInput: String,
        address: String?,
        chargeSiteUI: ChargeSiteUI
    ): SiteDetailState.Success {
        val chargePoints = chargeSiteUI.chargePoints
            .mapIndexed { index, cp ->
                val isFiltered = isChargePointFiltered(
                    searchInput = searchInput,
                    evseId = cp.shortenedEvseId,
                    availability = cp.availability,
                    pricePerKwh = cp.pricePerKwh,
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

        return SiteDetailState.Success(
            searchInput = searchInput,
            address = address,
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
