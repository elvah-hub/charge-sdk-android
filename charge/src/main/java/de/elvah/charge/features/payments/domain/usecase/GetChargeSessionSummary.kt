package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class GetChargeSessionSummary(
    private val chargeService: ChargeService,
) {

    operator fun invoke(): Flow<PaymentSummary?> = flow {
        emit(chargeService.getSummary())
    }
}
