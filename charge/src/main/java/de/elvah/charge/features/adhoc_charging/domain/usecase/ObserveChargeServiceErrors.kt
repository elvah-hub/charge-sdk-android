package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.service.charge.errors.ChargeError
import kotlinx.coroutines.flow.StateFlow

internal class ObserveChargeServiceErrors(
    private val chargeService: ChargeService,
) {

    operator fun invoke(): StateFlow<ChargeError?> {
        return chargeService.errors
    }
}
