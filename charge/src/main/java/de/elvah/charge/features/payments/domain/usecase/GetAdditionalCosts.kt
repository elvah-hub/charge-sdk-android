package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository

internal class GetAdditionalCosts(
    private val chargingRepository: ChargingRepository,
) {

    suspend operator fun invoke() = chargingRepository.getAdditionalCosts()
}
