package de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart

import de.elvah.charge.features.payments.domain.model.OrganisationDetails

sealed class ChargingStartState {
    data object Loading : ChargingStartState()
    data object Error : ChargingStartState()
    internal data class Success(
        val evseId: String,
        val organisationDetails: OrganisationDetails,
        val shouldShowAuthorizationBanner: Boolean = true,
        val error: Boolean = false
    ) : ChargingStartState()
    data object StartRequest : ChargingStartState()
}
