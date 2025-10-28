package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeState
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingState
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import de.elvah.charge.features.adhoc_charging.ui.mapper.toUI
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts
import de.elvah.charge.features.payments.domain.usecase.GetAdditionalCosts
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

internal class ActiveChargingViewModel(
    observeChargingSession: ObserveChargingSession,
    observeChargingState: ObserveChargingState,
    private val stopChargingSession: StopChargingSession,
    private val getOrganisationDetails: GetOrganisationDetails,
    private val getAdditionalCosts: GetAdditionalCosts,
) : ViewModel() {

    private var lastActiveSession: ActiveChargingSessionUI? = null

    internal val state: StateFlow<ActiveChargingState> =
        combine(
            observeChargingState(),
            observeChargingSession(),
        ) { state, chargeSession ->
            when {
                chargeSession != null -> getState(chargeSession, state)

                else -> ActiveChargingState.Error(
                    status = SessionStatus.START_REQUESTED,
                    cpoLogo = "", // TODO: organisationDetails.logoUrl
                )

            }

        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ActiveChargingState.Loading,
        )

    suspend fun getState(
        chargeSession: ChargingSession,
        chargeState: ChargeState,
    ): ActiveChargingState {
        val organisationDetails = getOrganisationDetails() ?: OrganisationDetails(
            privacyUrl = "",
            termsOfConditionUrl = "",
            companyName = "",
            logoUrl = "",
            supportContacts = SupportContacts(
                email = "",
                whatsapp = "",
                phone = "",
                agent = "",
            )
        )

        // TODO: handle this
        if (chargeState == ChargeState.SUMMARY) {
            return ActiveChargingState.SessionSummary
        }

        return when (chargeSession.status) {
            SessionStatus.START_REQUESTED,
            SessionStatus.STARTED,
            SessionStatus.CHARGING,
            SessionStatus.STOP_REQUESTED,
                -> {
                lastActiveSession =
                    (state.value as? ActiveChargingState.Success)?.activeChargingSessionUI

                ActiveChargingState.Success(
                    activeChargingSessionUI = ActiveChargingSessionUI(
                        evseId = chargeSession.evseId,
                        status = chargeSession.status,
                        consumption = chargeSession.consumption,
                        duration = chargeSession.duration,
                        error = false,
                        cpoLogo = organisationDetails.logoUrl
                    ),
                    additionalCostsUI = getAdditionalCosts()?.toUI(),
                    organisationDetails = organisationDetails
                )
            }

            SessionStatus.STOP_REJECTED,
            SessionStatus.START_REJECTED -> {
                ActiveChargingState.Error(
                    status = chargeSession.status,
                    cpoLogo = organisationDetails.logoUrl
                )
            }

            SessionStatus.STOPPED -> {
                ActiveChargingState.SessionSummary
            }
        }
    }

    fun stopCharging() {
        viewModelScope.launch {
            stopChargingSession()
        }
    }

    fun forceStopChargingAndClear() {
        viewModelScope.launch {
            stopChargingSession()
        }
    }

    fun onDismissError() {
        /* viewModelScope.launch {
            _state.update {
                ActiveChargingState.Success(
                    activeChargingSessionUI = (state.value as ActiveChargingState.Success).activeChargingSessionUI.copy(
                        error = false
                    ),
                    additionalCostsUI = getAdditionalCosts()?.toUI(),
                    organisationDetails = getOrganisationDetails()!!
                )
            }
        }*/
    }

    fun retry(status: SessionStatus) {
        if (status == SessionStatus.STOP_REQUESTED) {
            stopCharging()
        }
    }
}
