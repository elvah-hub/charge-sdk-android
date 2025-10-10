package de.elvah.charge.features.payments.domain.usecase

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
    private val sitesRepository: SitesRepository, // TODO: create a use case, see todo below
    private val chargingStore: ChargingStore,
) {

    suspend operator fun invoke(
        siteId: String,
        evseId: String
    ): Either<PaymentConfigErrors, PaymentConfiguration> =
        coroutineScope {
            val publishableKey = async { paymentsRepository.getPublishableKey() }

            // TODO: sites repo always makes a request to get the signed offer, we could
            // move this endpoint to a singleton repository and create a use case, this will
            // always require a siteId and evseId
            val signedOffer = async { sitesRepository.getSignedOffer(siteId, evseId) }

            signedOffer.await().flatMap {
                it.evses.firstOrNull()?.offer?.signedOffer?.let { signedOffer ->
                    paymentsRepository.createPaymentIntent(signedOffer)
                } ?: Either.Left(Exception("No offer found"))
            }.mapLeft {
                PaymentConfigErrors.NoOfferFound(it.cause)
            }.flatMap { paymentIntent ->
                chargingStore.setPaymentId(paymentIntent.paymentId)
                chargingStore.setEvseId(evseId)

                publishableKey.await().map { publishableKey ->
                    PaymentConfiguration(
                        publishableKey = publishableKey,
                        accountId = paymentIntent.accountId,
                        clientSecret = paymentIntent.clientSecret,
                        paymentId = paymentIntent.paymentId
                    )
                }.mapLeft {
                    PaymentConfigErrors.NoPublishableKey(it.cause)
                }
            }
        }
}

internal sealed class PaymentConfigErrors(val throwable: Throwable?) {
    data class NoPublishableKey(val cause: Throwable?) : PaymentConfigErrors(cause)
    data class NoOfferFound(val cause: Throwable?) : PaymentConfigErrors(cause)
}
