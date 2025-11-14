package de.elvah.charge.features.payments.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository

internal class GetPublishableKey(
    private val paymentsRepository: PaymentsRepository,
    private val chargingStore: ChargingStore
) {
    suspend operator fun invoke(): Either<Throwable, String> {
        val cachedKey = chargingStore.getPublishableKey()
        return if (cachedKey != null) {
            Either.Right(cachedKey)
        } else {
            paymentsRepository.getPublishableKey()
                .onRight { key ->
                    chargingStore.setPublishableKey(key)
                }
        }
    }
}