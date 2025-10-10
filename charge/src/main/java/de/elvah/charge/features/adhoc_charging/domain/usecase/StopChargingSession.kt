package de.elvah.charge.features.adhoc_charging.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.data.service.ChargeService

internal class StopChargingSession(
    private val chargeService: ChargeService,
) {

    suspend operator fun invoke(): Either<SessionExceptions, Boolean> {
        return chargeService.stopChargingSession()
    }
}
