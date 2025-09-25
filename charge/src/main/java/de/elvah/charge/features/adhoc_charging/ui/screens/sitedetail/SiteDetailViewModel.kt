package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.state.BuildSiteDetailSuccessState
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.features.sites.ui.mapper.toUI
import de.elvah.charge.platform.ui.navigation.asFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

internal class SiteDetailViewModel(
    sitesRepository: SitesRepository,
    savedStateHandle: SavedStateHandle,
    private val buildSiteDetailSuccessState: BuildSiteDetailSuccessState,
) : ViewModel() {

    private var chargePointSearchInput = MutableStateFlow("")

    val state = savedStateHandle.asFlow<AdHocChargingScreens.SiteDetailRoute>()
        .combine(chargePointSearchInput) { args, searchInput ->
            sitesRepository.getChargeSite(args.siteId)
                .map { it.toUI() }
                .fold(
                    ifLeft = { SiteDetailState.Error },
                    ifRight = { buildSiteDetailSuccessState(searchInput, it) }
                )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SiteDetailState.Loading,
        )

    internal fun onChargePointSearchInputChange(input: String) {
        chargePointSearchInput.value = input
    }
}
