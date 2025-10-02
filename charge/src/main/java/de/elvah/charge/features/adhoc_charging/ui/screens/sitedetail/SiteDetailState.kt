package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.ui.model.ChargePointUI
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI
import kotlinx.datetime.LocalDateTime

internal sealed class SiteDetailState {
    data object Loading : SiteDetailState()
    data object Error : SiteDetailState()

    internal data class Success(
        val campaignExpireAt: LocalDateTime?,
        val operatorName: String,
        val address: String?,
        val searchInput: String,
        val pricingForChargePoints: List<ChargePointItemUI>,
        val chargeSiteUI: ChargeSiteUI,
    ) : SiteDetailState()
}

internal data class ChargePointItemUI(
    val chargePointUI: ChargePointUI,
    val standardPricePerKwh: Price,
    val todayPricePerKwh: Price,
    val hasDiscount: Boolean,
)
