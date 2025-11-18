package de.elvah.charge.features.payments.domain.manager

import de.elvah.charge.features.payments.domain.model.GooglePayState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

internal class GooglePayManager {

    private val _paymentState = MutableStateFlow<GooglePayState>(GooglePayState.Idle)
    val paymentState: StateFlow<GooglePayState> = _paymentState.asStateFlow()

    fun processPaymentResult(result: GooglePayState) {
        _paymentState.value = result
    }

    fun setProcessingState() {
        _paymentState.value = GooglePayState.Processing
    }

    fun resetPaymentState() {
        _paymentState.value = GooglePayState.Idle
    }
}