package de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart

internal sealed class ChargingStartState {
    data object Loading : ChargingStartState()
    data object Error : ChargingStartState()
    internal data class Success(
        val evseId: String,
        val organizationLogoUrl: String?,
        val shouldShowAuthorizationBanner: Boolean = true,
        val error: Boolean = false
    ) : ChargingStartState()
}
