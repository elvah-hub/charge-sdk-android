package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.domain.usecase.FetchChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StartChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
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
    private val startChargingSession: StartChargingSession,
    private val stopChargingSession: StopChargingSession,
    private val fetchChargingSession: FetchChargingSession,
    private val getOrganisationDetails: GetOrganisationDetails,
) : ViewModel() {

    private val _state = MutableStateFlow<ActiveChargingState>(ActiveChargingState.Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            startChargingSession()

            val organisationDetails = getOrganisationDetails()
            organisationDetails?.let {
                _state.value = ActiveChargingState.Waiting(organisationDetails)
                startPolling()

                observeChargingSession().collect { chargeSession ->
                    val organisationDetails = getOrganisationDetails()

                    organisationDetails?.let { organisationDetails ->
                        when (chargeSession?.status1) {
                            SessionStatus.START_REQUESTED, SessionStatus.STARTED, null -> {
                                _state.update {
                                    ActiveChargingState.Waiting(organisationDetails)
                                }

                            }

                            SessionStatus.STOP_REQUESTED -> {
                                _state.update {
                                    ActiveChargingState.Stopping(organisationDetails)
                                }
                            }

                            SessionStatus.STOP_REJECTED,
                            SessionStatus.START_REJECTED -> {
                                _state.update {
                                    ActiveChargingState.Error
                                }
                            }

                            SessionStatus.CHARGING -> {
                                _state.update {
                                    val currentValue =
                                        (state.value as? ActiveChargingState.Active)?.activeChargingSessionUI

                                    if (currentValue != null) {
                                        ActiveChargingState.Active(
                                            currentValue.copy(
                                                evseId = chargeSession.evseId,
                                                status = chargeSession.status1.name,
                                                consumption = chargeSession.consumption,
                                                duration = chargeSession.duration
                                            )
                                        )
                                    } else {
                                        ActiveChargingState.Active(
                                            ActiveChargingSessionUI(
                                                evseId = chargeSession.evseId,
                                                status = chargeSession.status1.name,
                                                consumption = chargeSession.consumption,
                                                duration = chargeSession.duration,
                                                error = false,
                                                cpoLogo = organisationDetails.logoUrl
                                            )
                                        )
                                    }
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
                    _state.value = ActiveChargingState.Stopping(organisationDetails)
                } else {
                    val currentValue =
                        (state.value as ActiveChargingState.Active).activeChargingSessionUI
                    _state.update {
                        ActiveChargingState.Active(
                            currentValue.copy(
                                error = true
                            )
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
                ActiveChargingState.Active(
                    (state.value as ActiveChargingState.Active).activeChargingSessionUI.copy(
                        error = false
                    )
                )
            }
        }
    }
}

private const val DELAY_IN_MILLIS = 2000L
