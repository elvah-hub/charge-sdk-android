package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import de.elvah.charge.features.deals.ui.model.DealUI

internal sealed class SiteDetailState {
    data object Loading : SiteDetailState()
    data object Error : SiteDetailState()
    internal data class Success(val dealUI: DealUI) : SiteDetailState()
}