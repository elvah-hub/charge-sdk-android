package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargingSessionState
import kotlinx.coroutines.flow.StateFlow

internal class ObserveChargeSessionState(
    private val chargeService: ChargeService,
) {

    operator fun invoke(): StateFlow<ChargingSessionState> {
        return chargeService.chargeSessionState
    }
}
