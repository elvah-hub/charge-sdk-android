package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import de.elvah.charge.components.sitessource.InternalSitesSource
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargeSessionState
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.state.BuildSiteDetailSuccessState
import de.elvah.charge.features.sites.domain.extension.fullAddress
import de.elvah.charge.features.sites.domain.extension.getSlotAtTime
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.ScheduledPricing
import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class SiteDetailViewModel(
    sitesSource: SitesSource,
    savedStateHandle: SavedStateHandle,
    private val buildSiteDetailSuccessState: BuildSiteDetailSuccessState,
    observeChargeSessionState: ObserveChargeSessionState,
) : ViewModel() {

    private val args: AdHocChargingScreens.SiteDetailRoute =
        savedStateHandle.toRoute()

    private val internalSitesSource = sitesSource as InternalSitesSource

    private val siteId = args.siteId

    private val loading = MutableStateFlow(false)

    private val site = MutableStateFlow<ChargeSite?>(null)
    private val pricing = MutableStateFlow<ScheduledPricing?>(null)
    private val chargePointSearchInput = MutableStateFlow("")
    private val timeSlot = MutableStateFlow<ScheduledPricing.TimeSlot?>(null)

    internal val chargeSessionState = observeChargeSessionState()

    internal val state = combine(
        loading,
        site,
        pricing,
        timeSlot,
        chargePointSearchInput,
    ) { loading, site, pricing, timeSlot, searchInput ->
        when {
            loading -> SiteDetailState.Loading

            site != null && pricing != null -> buildSiteDetailSuccessState(
                chargeSite = site,
                pricing = pricing,
                timeSlot = timeSlot,
                searchInput = searchInput,
                address = site.address.fullAddress,
            )

            else -> SiteDetailState.Error
        }

    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SiteDetailState.Loading,
    )

    init {
        viewModelScope.launch {
            loading.value = true

            updateChargePointAvailabilities()
            updateSite()

            pricing.value = internalSitesSource.getSiteScheduledPricing(siteId).getOrNull()

            timeSlot.value = pricing.value?.dailyPricing?.today?.timeSlots?.getSlotAtTime()

            loading.value = false
        }
    }

    internal fun refreshAvailability() {
        viewModelScope.launch {
            updateChargePointAvailabilities()
            updateSite()
        }
    }

    private fun updateSite() {
        site.value = internalSitesSource.getSite(siteId).getOrNull()
    }

    private suspend fun updateChargePointAvailabilities() {
        internalSitesSource.updateSiteAvailability(siteId)
    }


    internal fun onChargePointSearchInputChange(input: String) {
        chargePointSearchInput.value = input
    }

    internal fun updateTimeSlot() {
        timeSlot.value = pricing.value?.dailyPricing?.today?.timeSlots?.getSlotAtTime()
    }
}
