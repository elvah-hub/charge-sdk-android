package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.data.service.ChargeService
import kotlinx.coroutines.flow.last

internal class HasActiveChargingSession(
    private val chargeService: ChargeService,
) {

    suspend operator fun invoke(): Boolean {
        return chargeService.activeSession.last() != null
    }
}
