package de.elvah.charge.features.sites.ui

import de.elvah.charge.features.sites.ui.model.ChargeSiteUI

internal sealed class SitesState {
    data object Loading : SitesState()
    data class Success(val site: ChargeSiteUI) : SitesState()
    data class ActiveSession(val site: ChargeSiteUI) : SitesState()
    data object Error : SitesState()
}
