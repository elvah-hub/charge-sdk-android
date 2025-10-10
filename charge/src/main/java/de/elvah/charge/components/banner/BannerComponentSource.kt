package de.elvah.charge.components.banner

import de.elvah.charge.features.sites.ui.SitesState
import kotlinx.coroutines.flow.StateFlow

internal interface BannerComponentSource {

    val state: StateFlow<SitesState>
}
