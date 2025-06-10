package de.elvah.charge.features.deals.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.domain.usecase.HasActiveChargingSession
import de.elvah.charge.features.deals.domain.usecase.GetDeal
import de.elvah.charge.features.deals.domain.usecase.GetLocation
import de.elvah.charge.features.deals.ui.mapper.toUI
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

internal class DealsViewModel(
    private val getDeal: GetDeal,
    private val hasActiveSession: HasActiveChargingSession,
    getLocation: GetLocation,
) : ViewModel() {

    val state = getLocation()
        .map {
            getDeal(GetDeal.Params(it.minLat, it.minLng, it.maxLat, it.maxLng))
        }
        .map {
            val activeSession = hasActiveSession()

            it.getOrNull()?.let {
                if (activeSession) {
                    DealsState.ActiveSession(it.toUI())
                } else {
                    DealsState.Success(it.toUI())
                }
            } ?: run {
                DealsState.Error
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DealsState.Loading
        )
}
