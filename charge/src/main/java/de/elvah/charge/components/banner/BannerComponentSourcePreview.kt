package de.elvah.charge.components.banner

import de.elvah.charge.features.sites.ui.SitesState
import de.elvah.charge.features.sites.ui.model.ChargeBannerRender
import de.elvah.charge.features.sites.ui.model.Location
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class BannerComponentSourcePreview : BannerComponentSource {

    private val _state = MutableStateFlow<SitesState>(getSuccessState())
    override val state: StateFlow<SitesState> = _state

    private fun getSuccessState() = SitesState.Success(
        site = ChargeBannerRender(
            id = "testing",
            cpoName = "CPO: Testing GmbH",
            address = "local address 1 Germany",
            location = Location(
                lat = 0.0,
                lng = 0.0
            ),
            campaignEnd = "2025-04-23T10:21:28.405000000Z",
            originalPrice = 0.50,
            price = 0.24,
        ),
    )
}
