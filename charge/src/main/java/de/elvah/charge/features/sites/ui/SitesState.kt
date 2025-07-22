package de.elvah.charge.features.sites.ui

import de.elvah.charge.features.deals.ui.components.ChargeBannerActiveSessionRender
import de.elvah.charge.features.sites.ui.model.ChargeBannerRender

internal sealed class SitesState {
    data object Loading : SitesState()
    data class Success(val site: ChargeBannerRender) : SitesState()
    data class ActiveSession(val site: ChargeBannerActiveSessionRender) : SitesState()
    data object Empty : SitesState()
    data object Error : SitesState()
}
