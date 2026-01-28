package de.elvah.charge.features.payments.domain.manager

import de.elvah.charge.features.payments.domain.model.GooglePayState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class GooglePayManager {

    private val _state = MutableStateFlow<GooglePayState>(GooglePayState.Unavailable)
    val state: StateFlow<GooglePayState> = _state.asStateFlow()

    fun setGooglePayAvailable(isAvailable: Boolean) {
        _state.value = if (isAvailable) {
            // Transition to Idle when becoming available, unless already in a different state
            if (_state.value is GooglePayState.Unavailable) {
                GooglePayState.Idle
            } else {
                _state.value // Preserve current state if already available
            }
        } else {
            GooglePayState.Unavailable
        }
    }

    fun processPaymentResult(result: GooglePayState) {
        // Only process payment results if Google Pay is available
        if (_state.value !is GooglePayState.Unavailable) {
            _state.value = result
        }
    }

    fun setProcessingState() {
        // Only set processing if Google Pay is available
        if (_state.value !is GooglePayState.Unavailable) {
            _state.value = GooglePayState.Processing
        }
    }

    fun resetPaymentState() {
        // Reset to Idle only if Google Pay is available
        if (_state.value !is GooglePayState.Unavailable) {
            _state.value = GooglePayState.Idle
        }
    }
}