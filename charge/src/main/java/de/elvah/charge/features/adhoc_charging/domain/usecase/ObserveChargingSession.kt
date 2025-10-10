package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import kotlinx.coroutines.flow.StateFlow

internal class ObserveChargingSession(
    private val chargeService: ChargeService,
) {

    operator fun invoke(): StateFlow<ChargeSession?> {
        return chargeService.chargeSession
    }
}
