package de.elvah.charge.features.payments.domain.model

internal sealed class GooglePayState {
    /**
     * Google Pay is not available on this device or not ready
     */
    data object Unavailable : GooglePayState()

    /**
     * Google Pay is available and ready to use
     */
    data object Idle : GooglePayState()

    /**
     * Payment is being processed
     */
    data object Processing : GooglePayState()

    /**
     * Payment completed successfully
     */
    data object Success : GooglePayState()

    /**
     * Payment was cancelled by the user
     */
    data object Cancelled : GooglePayState()

    /**
     * Payment failed with an error
     */
    data class Failed(val error: String) : GooglePayState()
}

/**
 * Extension to check if Google Pay is available
 */
internal val GooglePayState.isAvailable: Boolean
    get() = this !is GooglePayState.Unavailable
