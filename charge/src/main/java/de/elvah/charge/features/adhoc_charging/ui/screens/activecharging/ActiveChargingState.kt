package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import de.elvah.charge.features.payments.domain.model.OrganisationDetails

sealed class ActiveChargingState {
    data object Loading : ActiveChargingState()
    data object Error : ActiveChargingState()
    internal data class Active(
        val activeChargingSessionUI: ActiveChargingSessionUI,
    ) : ActiveChargingState()

    internal data class Waiting(
        val organisationDetails: OrganisationDetails,
    ) : ActiveChargingState()

    internal data class Stopping(
        val organisationDetails: OrganisationDetails,
    ) : ActiveChargingState()

    internal data class Stopped(
        val organisationDetails: OrganisationDetails,
    ) : ActiveChargingState()
}