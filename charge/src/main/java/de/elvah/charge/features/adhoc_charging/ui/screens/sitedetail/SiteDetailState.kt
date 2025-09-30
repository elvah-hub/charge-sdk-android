package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import de.elvah.charge.features.sites.ui.model.ChargeSiteUI

internal sealed class SiteDetailState {
    data object Loading : SiteDetailState()
    data object Error : SiteDetailState()

    internal data class Success(
        val searchInput: String,
        val address: String?,
        val chargeSiteUI: ChargeSiteUI,
    ) : SiteDetailState()
}
