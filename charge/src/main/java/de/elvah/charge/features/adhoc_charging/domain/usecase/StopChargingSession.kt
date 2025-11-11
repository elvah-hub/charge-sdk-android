package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService

internal class StopChargingSession(
    private val chargeService: ChargeService,
) {

    operator fun invoke() {
        return chargeService.stopSession()
    }
}
