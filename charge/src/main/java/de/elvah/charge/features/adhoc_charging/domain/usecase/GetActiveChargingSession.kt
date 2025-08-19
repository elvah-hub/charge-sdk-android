package de.elvah.charge.features.adhoc_charging.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository


internal class GetActiveChargingSession(
    private val chargingRepository: ChargingRepository,
) {

    suspend operator fun invoke(): Either<Throwable, ChargingSession> {
        return chargingRepository.fetchChargingSession()
    }
}
