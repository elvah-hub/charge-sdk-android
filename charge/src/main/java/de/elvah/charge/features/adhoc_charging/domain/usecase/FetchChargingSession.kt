package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository


internal class FetchChargingSession(
    private val chargingRepository: ChargingRepository,
) {

    suspend operator fun invoke() {
        chargingRepository.fetchChargingSession()
    }
}