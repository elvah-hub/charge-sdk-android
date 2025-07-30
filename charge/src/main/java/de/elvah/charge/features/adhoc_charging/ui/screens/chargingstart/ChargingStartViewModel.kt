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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class ChargingStartViewModel(
    private val getPaymentToken: GetPaymentToken,
    private val getOrganisationDetails: GetOrganisationDetails,
    private val chargingRepository: ChargingRepository,
    private val startChargingSession: StartChargingSession,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val _state = MutableStateFlow<ChargingStartState>(ChargingStartState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val route = savedStateHandle.toRoute<AdHocChargingScreens.ChargingStartRoute>()

            val result = getOrganisationDetails()
            val token = getPaymentToken(route.paymentId)

            chargingRepository.updateChargingToken(token.getOrNull().orEmpty())

            if (result != null) {
                _state.value = ChargingStartState.Success(
                    evseId = route.evseId,
                    organisationDetails = result
                )
            } else {
                _state.value = ChargingStartState.Error
            }
        }
    }

    fun closeBanner() {
        viewModelScope.launch {
            _state.value =
                (_state.value as ChargingStartState.Success).copy(shouldShowAuthorizationBanner = false)
        }
    }

    fun startChargeSession() {
        viewModelScope.launch {
            val result = startChargingSession()

            if (result.isRight()) {
                _state.value = ChargingStartState.StartRequest
            } else {
                val route = savedStateHandle.toRoute<AdHocChargingScreens.ChargingStartRoute>()
                val organisationDetails = getOrganisationDetails()

                organisationDetails?.let { it1 ->
                    _state.update {
                        ChargingStartState.Success(
                            evseId = route.evseId,
                            organisationDetails = it1,
                            error = true
                        )
                    }
                }
            }
        }
    }

    fun onDismissError() {
        viewModelScope.launch {
            if (_state.value is ChargingStartState.Success){
                _state.update {
                    ChargingStartState.Success(
                        evseId = (_state.value as ChargingStartState.Success).evseId,
                        organisationDetails = (_state.value as ChargingStartState.Success).organisationDetails,
                        error = false
                    )
                }
            }
        }
    }
}
