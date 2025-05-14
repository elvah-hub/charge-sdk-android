package de.elvah.charge.features.payments.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository


internal class GetPaymentSummary(
    private val paymentsRepository: PaymentsRepository,
) {

    suspend operator fun invoke(paymentId: String): Either<Exception, PaymentSummary> {
        return paymentsRepository.getPaymentSummary(paymentId)
    }
}