package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.data.service.ChargeService

internal class ClearLocalSessionData(
    private val chargeService: ChargeService,
) {
    suspend operator fun invoke() {
        chargeService.resetSession()
    }
}
