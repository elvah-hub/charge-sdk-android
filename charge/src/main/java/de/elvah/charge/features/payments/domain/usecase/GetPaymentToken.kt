package de.elvah.charge.features.payments.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository


internal class GetPaymentToken(
    private val paymentsRepository: PaymentsRepository,
) {

    suspend operator fun invoke(paymentId: String): Either<Exception, String> =
        paymentsRepository.authorizeSession(paymentId)
}