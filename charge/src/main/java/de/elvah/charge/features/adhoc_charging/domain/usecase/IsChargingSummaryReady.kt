package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class IsChargingSummaryReady(
    private val chargeService: ChargeService,
) {

    operator fun invoke(): Flow<Boolean> {
        return chargeService.state.map {
            it == ChargeState.SUMMARY
        }
    }
}
