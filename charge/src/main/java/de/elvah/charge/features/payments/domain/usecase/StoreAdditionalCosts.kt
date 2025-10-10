package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.sites.domain.model.AdditionalCosts

internal class StoreAdditionalCosts(
    private val chargingRepository: ChargingRepository,
) {

    suspend operator fun invoke(additionalCosts: AdditionalCosts?) =
        chargingRepository.storeAdditionalCosts(additionalCosts)
}
