package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.data.service.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import kotlinx.coroutines.flow.Flow

internal class ObserveChargingSession(
    private val chargeService: ChargeService,
) {

    operator fun invoke(): Flow<ChargingSession?> {
        return chargeService.activeSession
    }
}
