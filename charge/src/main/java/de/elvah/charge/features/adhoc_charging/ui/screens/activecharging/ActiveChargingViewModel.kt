package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.domain.usecase.FetchChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StartChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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

                observeChargingSession().collect {
                    val organisationDetails = getOrganisationDetails()

                    when (state.value) {
                        is ActiveChargingState.Waiting -> {
                            if (it != null) {
                                _state.value = ActiveChargingState.Active(
                                    ActiveChargingSessionUI(
                                        evseId = it.evseId,
                                        status = it.status,
                                        consumption = it.consumption,
                                        duration = it.duration,
                                        cpoLogo = organisationDetails?.logoUrl.orEmpty()
                                    )
                                )
                            }
                        }


                        is ActiveChargingState.Active -> {
                            if (it != null) {
                                _state.value = ActiveChargingState.Active(
                                    ActiveChargingSessionUI(
                                        evseId = it.evseId,
                                        status = it.status,
                                        consumption = it.consumption,
                                        duration = it.duration,
                                        cpoLogo = organisationDetails?.logoUrl.orEmpty()
                                    )
                                )
                            }
                        }

                        ActiveChargingState.Error -> {

                        }

                        ActiveChargingState.Loading -> {

                        }

                        else -> {

                        }
                    }
                }
            }
        }
    }

    fun stopCharging() {
        viewModelScope.launch {
            stopChargingSession()

            val organisationDetails = getOrganisationDetails()

            organisationDetails?.let {
                _state.value = ActiveChargingState.Stopping(organisationDetails)
                delay(5000)
                _state.value = ActiveChargingState.Stopped(organisationDetails)
            }
        }
    }

    private fun startPolling() {
        viewModelScope.launch {
            while (isActive && (state.value is ActiveChargingState.Waiting || state.value is ActiveChargingState.Active)) {
                fetchChargingSession()
                delay(DELAY_IN_MILLIS)
            }
        }
    }
}

private const val DELAY_IN_MILLIS = 2000L
