package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.ui.model.ChargePointUI
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
    ) : SiteDetailState()
}

internal data class ChargePointItemUI(
    val chargePointUI: ChargePointUI,
    val standardPricePerKwh: Price,
    val todayPricePerKwh: Price,
    val hasDiscount: Boolean,
    val powerType: String?,
)
