package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.state.BuildSiteDetailSuccessState
import de.elvah.charge.features.sites.domain.extension.fullAddress
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.features.sites.domain.usecase.GetSiteScheduledPricing
import de.elvah.charge.features.sites.ui.mapper.toUI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class SiteDetailViewModel(
    private val sitesRepository: SitesRepository,
    savedStateHandle: SavedStateHandle,
    private val buildSiteDetailSuccessState: BuildSiteDetailSuccessState,
    private val getSiteScheduledPricing: GetSiteScheduledPricing,
) : ViewModel() {

    private val args: AdHocChargingScreens.SiteDetailRoute =
        savedStateHandle.toRoute()

    val siteId = args.siteId

    private val site = MutableStateFlow<ChargeSite?>(null)
    private val pricing = MutableStateFlow<ScheduledPricing?>(null)
    private val chargePointSearchInput = MutableStateFlow("")

    val state = combine(
        site,
        pricing,
        chargePointSearchInput,
    ) { site, pricing, searchInput ->
        if (site == null) return@combine SiteDetailState.Error
        if (pricing == null) return@combine SiteDetailState.Error

        buildSiteDetailSuccessState(
            chargeSiteUI = site.toUI(),
            pricing = pricing,
            searchInput = searchInput,
            address = site.address.fullAddress,
        )

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SiteDetailState.Loading,
    )

    init {
        viewModelScope.launch {
            site.value = sitesRepository.getChargeSite(args.siteId)
                .fold({ null }, { it })

            pricing.value = getSiteScheduledPricing(GetSiteScheduledPricing.Params(siteId = siteId))
                .fold({ null }, { it })
        }
    }

    internal fun refreshAvailability() {
        viewModelScope.launch {
            sitesRepository.updateChargePointAvailabilities(
                siteId = siteId,
            ).fold(
                ifLeft = { /* keep same data */ },
                ifRight = { evses ->
                    site.update {
                        it?.copy(
                            evses = evses,
                        )
                    }
                }
            )
        }
    }

    internal fun onChargePointSearchInputChange(input: String) {
        chargePointSearchInput.value = input
    }
}
