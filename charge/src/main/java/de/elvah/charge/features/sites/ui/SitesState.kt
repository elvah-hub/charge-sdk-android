package de.elvah.charge.features.sites.ui

import de.elvah.charge.features.sites.ui.components.ChargeBannerActiveSessionRender
import de.elvah.charge.features.sites.ui.model.ChargeBannerRender

public sealed class SitesState {
    public data object Loading : SitesState()
    public data class Success(val site: ChargeBannerRender) : SitesState()
    public data class ActiveSession(val site: ChargeBannerActiveSessionRender) : SitesState()
    public data object Empty : SitesState()
    public data object Error : SitesState()
}
