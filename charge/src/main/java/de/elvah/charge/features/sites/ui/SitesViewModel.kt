package de.elvah.charge.features.sites.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.domain.usecase.HasActiveChargingSession
import de.elvah.charge.features.sites.domain.usecase.GetFilters
import de.elvah.charge.features.sites.domain.usecase.GetSite
import de.elvah.charge.features.sites.ui.mapper.toUI
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

internal class SitesViewModel(
    private val getSite: GetSite,
    private val hasActiveSession: HasActiveChargingSession,
    getFilters: GetFilters,
) : ViewModel() {

    val state = getFilters()
        .onEach {
            Log.d("HOLA", it.toString())
        }
        .map {
            getSite(
                GetSite.Params(
                    boundingBox = it.boundingBox,
                    campaignId = it.campaignId,
                    organisationId = it.organisationId,
                    offerType = it.offerType
                )
            )
        }
        .map {
            val activeSession = hasActiveSession()

            it.getOrNull()?.let {
                if (activeSession) {
                    SitesState.ActiveSession(it.toUI())
                } else {
                    SitesState.Success(it.toUI())
                }
            } ?: run {
                SitesState.Error
            }

        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SitesState.Loading
        )
}
