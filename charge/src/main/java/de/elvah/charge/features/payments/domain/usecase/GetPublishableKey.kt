package de.elvah.charge.features.payments.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.payments.domain.model.PublishableKey
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository

internal class GetPublishableKey(
    private val paymentsRepository: PaymentsRepository,
) {
    suspend operator fun invoke(): Either<Throwable, PublishableKey> =
        paymentsRepository.getPublishableKey()
}
