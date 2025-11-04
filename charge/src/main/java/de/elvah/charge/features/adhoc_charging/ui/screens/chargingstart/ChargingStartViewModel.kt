package de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.usecase.StartChargingSession
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ChargingStartViewModel(
    private val getPaymentToken: GetPaymentToken,
    private val getOrganisationDetails: GetOrganisationDetails,
    private val chargingRepository: ChargingRepository,
    private val startChargingSession: StartChargingSession,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<AdHocChargingScreens.ChargingStartRoute>()

    private val _state = MutableStateFlow<ChargingStartState>(ChargingStartState.Loading)
    internal val state: StateFlow<ChargingStartState> = _state

    init {
        viewModelScope.launch {
            val organisationDetails = getOrganisationDetails()
            val token = getPaymentToken(route.paymentId)

            chargingRepository.updateChargingToken(token.getOrNull().orEmpty())

            if (organisationDetails != null) {
                _state.value = ChargingStartState.Success(
                    evseId = route.shortenedEvseId,
                    organisationDetails = organisationDetails
                )
            } else {
                _state.value = ChargingStartState.Error
            }
        }
    }

    fun closeBanner() {
        viewModelScope.launch {
            if (_state.value is ChargingStartState.Success) {
                val state = _state.value as ChargingStartState.Success

                _state.update {
                    state.copy(shouldShowAuthorizationBanner = false)
                }
            }
        }
    }

    fun startChargeSession() = startChargingSession()

    fun onDismissError() {
        viewModelScope.launch {
            if (_state.value is ChargingStartState.Success) {
                val state = _state.value as ChargingStartState.Success
                _state.update {
                    state.copy(error = false)
                }
            }
        }
    }
}
