package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import kotlinx.coroutines.flow.StateFlow

internal class GetChargingSession(
    private val chargeService: ChargeService,
) {

    operator fun invoke(): StateFlow<ChargingSession?> {
        return chargeService.chargeSession
    }
}
