package de.elvah.charge.features.adhoc_charging.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository


internal class StopChargingSession(
    private val chargingRepository: ChargingRepository,
) {

    suspend operator fun invoke(): Either<SessionExceptions, Boolean> =
        chargingRepository.stopChargingSession()
}
