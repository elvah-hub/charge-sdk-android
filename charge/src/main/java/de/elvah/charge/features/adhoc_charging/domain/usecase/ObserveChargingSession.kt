package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import kotlinx.coroutines.flow.Flow


internal class ObserveChargingSession(
    private val chargingRepository: ChargingRepository,
) {

    operator fun invoke(): Flow<ChargingSession?> {
        return chargingRepository.activeSessions
    }
}