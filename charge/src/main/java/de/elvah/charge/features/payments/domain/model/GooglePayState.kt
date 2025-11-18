package de.elvah.charge.features.payments.domain.model

internal sealed class GooglePayState {
    internal data object Idle : GooglePayState()
    internal data object Processing : GooglePayState()
    internal data object Success : GooglePayState()
    internal data object Cancelled : GooglePayState()
    internal data class Failed(val error: String) : GooglePayState()
}