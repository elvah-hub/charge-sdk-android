package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.features.sites.ui.mapper.toUI
import de.elvah.charge.platform.ui.navigation.asFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class SiteDetailViewModel(
    sitesRepository: SitesRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val state = savedStateHandle.asFlow<AdHocChargingScreens.SiteDetailRoute>()
        .map {
            sitesRepository.getChargeSite(it.siteId).map { it.toUI() }
                .fold(
                    ifLeft = { SiteDetailState.Error },
                    ifRight = { SiteDetailState.Success(it) }
                )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SiteDetailState.Loading)
}
