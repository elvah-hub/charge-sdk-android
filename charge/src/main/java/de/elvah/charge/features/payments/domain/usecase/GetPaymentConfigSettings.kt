package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.PaymentConfig

internal class GetPaymentConfigSettings(
    private val config: Config
) {
    operator fun invoke(): PaymentConfig = config.paymentConfig
}