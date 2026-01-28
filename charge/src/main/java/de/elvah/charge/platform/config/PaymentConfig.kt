package de.elvah.charge.platform.config

public data class PaymentConfig(
    public val googlePayEnabled: Boolean = true,
    public val creditCardEnabled: Boolean = true,
) {
    init {
        validatePaymentMethods()
    }

    private fun validatePaymentMethods() {
        if (!googlePayEnabled && !creditCardEnabled) {
            throw IllegalArgumentException(
                "At least one payment method must be enabled. Both googlePayEnabled and creditCardEnabled cannot be false."
            )
        }
    }
}