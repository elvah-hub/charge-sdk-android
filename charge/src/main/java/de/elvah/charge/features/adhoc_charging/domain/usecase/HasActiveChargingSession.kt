package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository


internal class HasActiveChargingSession(
    private val chargingRepository: ChargingRepository,
) {

    suspend operator fun invoke(): Boolean {
        return chargingRepository.fetchChargingSession().isRight()
    }
}