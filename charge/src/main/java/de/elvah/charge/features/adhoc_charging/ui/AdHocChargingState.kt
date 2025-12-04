package de.elvah.charge.features.adhoc_charging.ui

internal sealed class AdHocChargingState {
    data object Loading : AdHocChargingState()
    data object Error : AdHocChargingState()
    data class Success(val publishableKey: String) : AdHocChargingState()
}