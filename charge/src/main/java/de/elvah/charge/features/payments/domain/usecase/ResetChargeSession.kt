package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService

internal class ResetChargeSession(
    private val chargeService: ChargeService,
) {

    operator fun invoke() {
        chargeService.reset()
    }
}
