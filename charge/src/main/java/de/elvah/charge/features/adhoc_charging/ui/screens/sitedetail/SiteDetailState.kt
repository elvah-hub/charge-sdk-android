package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Pricing
import kotlinx.datetime.LocalDateTime

internal sealed class SiteDetailState {
    data object Loading : SiteDetailState()
    data object Error : SiteDetailState()

    internal data class Success(
        val discountExpiresAt: LocalDateTime?,
        val operatorName: String,
        val address: String?,
        val coordinates: Pair<Double, Double>,
        val searchInput: String,
        val chargePoints: List<ChargePointItemUI>,
        val noSearchResults: Boolean,
        val noStations: Boolean,
    ) : SiteDetailState()
}

internal data class ChargePointItemUI(
    val evseId: String,
    val shortenedEvseId: String,
    val availability: ChargePointAvailability,
    val standardPricePerKwh: Pricing,
    val todayPricePerKwh: Pricing,
    val maxPowerInKW: Float?,
    val powerType: String?,
    val hasDiscount: Boolean,
)
