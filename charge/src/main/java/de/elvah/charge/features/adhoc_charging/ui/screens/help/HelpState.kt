package de.elvah.charge.features.adhoc_charging.ui.screens.help

import de.elvah.charge.features.payments.domain.model.OrganisationDetails

internal sealed class HelpState {
    data object Loading : HelpState()
    data object Error : HelpState()
    internal data class Success(val organisationDetails: OrganisationDetails) : HelpState()
}