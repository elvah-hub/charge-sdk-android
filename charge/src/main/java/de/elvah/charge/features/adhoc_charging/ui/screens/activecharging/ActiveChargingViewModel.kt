package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.domain.usecase.FetchChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import de.elvah.charge.features.adhoc_charging.ui.mapper.toUI
import de.elvah.charge.features.payments.domain.usecase.GetAdditionalCosts
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class ActiveChargingViewModel(
    private val observeChargingSession: ObserveChargingSession,
    private val stopChargingSession: StopChargingSession,
    private val fetchChargingSession: FetchChargingSession,
    private val getOrganisationDetails: GetOrganisationDetails,
    private val getAdditionalCosts: GetAdditionalCosts,
) : ViewModel() {

    private val _state = MutableStateFlow<ActiveChargingState>(ActiveChargingState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val organisationDetails = getOrganisationDetails()
            organisationDetails?.let {
                startPolling()
                observeChargingSession().collect { chargeSession ->
                    val organisationDetails = getOrganisationDetails()

                    organisationDetails?.let { organisationDetails ->
                        when (chargeSession?.status) {
                            SessionStatus.START_REQUESTED,
                            SessionStatus.STARTED,
                            SessionStatus.CHARGING,
                            SessionStatus.STOP_REQUESTED,
                            null -> {
                                _state.update {
                                    chargeSession?.let {
                                        ActiveChargingState.Success(
                                            activeChargingSessionUI = ActiveChargingSessionUI(
                                                evseId = it.evseId,
                                                status = it.status,
                                                consumption = it.consumption,
                                                duration = it.duration,
                                                error = (_state.value as? ActiveChargingState.Success)?.activeChargingSessionUI?.error ?: false,
                                                cpoLogo = organisationDetails.logoUrl
                                            ),
                                            additionalCostsUI = getAdditionalCosts()?.toUI(),
                                            organisationDetails = organisationDetails
                                        )
                                    } ?: ActiveChargingState.Error(
                                        status = SessionStatus.START_REQUESTED,
                                        cpoLogo = organisationDetails.logoUrl
                                    )
                                }
                            }

                            SessionStatus.STOP_REJECTED,
                            SessionStatus.START_REJECTED -> {
                                _state.update {
                                    ActiveChargingState.Error(
                                        status = chargeSession.status,
                                        cpoLogo = organisationDetails.logoUrl
                                    )
                                }
                            }

                            SessionStatus.STOPPED -> {
                                _state.update {
                                    ActiveChargingState.Stopped(organisationDetails)
                                }
                                viewModelScope.cancel()
                            }
                        }
                    }
                }
            }
        }
    }

    fun stopCharging() {
        viewModelScope.launch {
            val result = stopChargingSession()

            val organisationDetails = getOrganisationDetails()

            organisationDetails?.let {
                if (result.isRight()) {

                } else {
                    val currentValue =
                        (state.value as ActiveChargingState.Success).activeChargingSessionUI
                    _state.update {
                        ActiveChargingState.Success(
                            activeChargingSessionUI = currentValue.copy(
                                error = true
                            ),
                            additionalCostsUI = getAdditionalCosts()?.toUI(),
                            organisationDetails = organisationDetails
                        )
                    }
                }
            }
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive && state.value !is ActiveChargingState.Error) {
                fetchChargingSession()
                delay(DELAY_IN_MILLIS)
            }
        }
    }

    fun onDismissError() {
        viewModelScope.launch {
            _state.update {
                ActiveChargingState.Success(
                    activeChargingSessionUI = (state.value as ActiveChargingState.Success).activeChargingSessionUI.copy(
                        error = false
                    ),
                    additionalCostsUI = getAdditionalCosts()?.toUI(),
                    organisationDetails = getOrganisationDetails()!!
                )
            }
        }
    }
}

private const val DELAY_IN_MILLIS = 2000L
