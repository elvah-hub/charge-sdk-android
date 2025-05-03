package de.elvah.charge.features.payments.domain.usecase

import arrow.core.Either
import arrow.core.flatMap
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope



internal class GetPaymentConfiguration(
    private val paymentsRepository: PaymentsRepository,
) {

    suspend operator fun invoke(signedOffer: String): Either<Exception, PaymentConfiguration> =
        coroutineScope {
            val publishableKey = async { paymentsRepository.getPublishableKey() }
            val paymentIntent = async { paymentsRepository.createPaymentIntent(signedOffer) }

            paymentIntent.await().flatMap { paymentIntent ->
                publishableKey.await().map {
                    PaymentConfiguration(
                        publishableKey = it,
                        accountId = paymentIntent.accountId,
                        clientSecret = paymentIntent.clientSecret,
                        paymentId = paymentIntent.paymentId
                    )
                }
            }
        }
}