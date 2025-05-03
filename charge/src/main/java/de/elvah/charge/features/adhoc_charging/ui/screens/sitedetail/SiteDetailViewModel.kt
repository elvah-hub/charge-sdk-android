package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens
import de.elvah.charge.features.deals.domain.repository.DealsRepository
import de.elvah.charge.features.deals.ui.mapper.toUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SiteDetailViewModel(
    dealsRepository: DealsRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _state = MutableStateFlow<SiteDetailState>(SiteDetailState.Loading)
    val state = _state.asStateFlow()

    init {
        val route = savedStateHandle.toRoute<AdHocChargingScreens.SiteDetailRoute>()

        viewModelScope.launch {
            val deal = dealsRepository.getDeal(route.dealId)

            _state.update {
                SiteDetailState.Success(
                    deal.toUI()
                )
            }
        }
    }
}
