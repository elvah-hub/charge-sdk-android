package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import de.elvah.charge.platform.core.mvi.Reducer

internal sealed class ChargingPointDetailEffect : Reducer.ViewEffect {
    class NavigateTo(val evseId: String, val paymentId: String) : ChargingPointDetailEffect()
}
