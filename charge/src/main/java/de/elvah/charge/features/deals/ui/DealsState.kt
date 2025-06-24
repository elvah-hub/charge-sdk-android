package de.elvah.charge.features.deals.ui

import de.elvah.charge.features.deals.ui.model.DealUI

internal sealed class DealsState {
    data object Loading : DealsState()
    internal data class Success(val deal: DealUI) : DealsState()
    internal data class ActiveSession(val deal: DealUI) : DealsState()
    data object Error : DealsState()
}