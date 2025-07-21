package de.elvah.charge.features.payments.domain.usecase

import android.util.Log
import arrow.core.Either
import arrow.core.flatMap
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


internal class GetPaymentConfiguration(
    private val paymentsRepository: PaymentsRepository,
    private val sitesRepository: SitesRepository,
    private val chargingStore: ChargingStore,
) {

    suspend operator fun invoke(
        siteId: String,
        evseId: String
    ): Either<Exception, PaymentConfiguration> =
        coroutineScope {
            val publishableKey = async { paymentsRepository.getPublishableKey() }
            val signedOffer = async { sitesRepository.getSignedOffer(siteId, evseId) }

            val paymentIntent = signedOffer.await().flatMap {
                it.evses.first().offer.signedOffer?.let { signedOffer ->
                    paymentsRepository.createPaymentIntent(signedOffer)
                } ?: Either.Left(Exception("No offer found"))
            }

            paymentIntent.flatMap { paymentIntent ->

                chargingStore.setPaymentId(paymentIntent.paymentId)
                chargingStore.setEvseId(evseId)

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
