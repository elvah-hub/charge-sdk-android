package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import de.elvah.charge.features.adhoc_charging.ui.model.AdditionalCostsUI
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.platform.simulator.data.repository.SessionStatus

internal sealed interface ActiveChargingState {
    data object Loading : ActiveChargingState

    sealed interface Error : ActiveChargingState {

        val cpoLogo: String

        data class StartFailed(
            override val cpoLogo: String,
        ) : Error

        data class OtherError(
            val status: SessionStatus,
            override val cpoLogo: String,
        ) : Error
    }

    data class Success(
        val activeChargingSessionUI: ActiveChargingSessionUI,
        val additionalCostsUI: AdditionalCostsUI?,
        val organisationDetails: OrganisationDetails,
    ) : ActiveChargingState

    data object SessionSummary : ActiveChargingState
}
