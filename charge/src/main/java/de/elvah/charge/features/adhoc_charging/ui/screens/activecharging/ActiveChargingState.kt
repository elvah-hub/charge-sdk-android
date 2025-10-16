package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import de.elvah.charge.features.adhoc_charging.ui.model.AdditionalCostsUI
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.platform.simulator.data.repository.SessionStatus

internal sealed class ActiveChargingState {
    data object Loading : ActiveChargingState()
    data class Error(
        val status: SessionStatus,
        val cpoLogo: String

    ) : ActiveChargingState()

    internal data class Success(
        val activeChargingSessionUI: ActiveChargingSessionUI,
        val additionalCostsUI: AdditionalCostsUI?,
        val organisationDetails: OrganisationDetails,
    ) : ActiveChargingState()

    internal data class Stopped(
        val organisationDetails: OrganisationDetails,
    ) : ActiveChargingState()
}
