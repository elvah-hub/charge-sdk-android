package de.elvah.charge.platform.simulator.data.repository

import arrow.core.Either
import de.elvah.charge.features.payments.domain.model.PaymentIntent
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.platform.core.arrow.extensions.toEither


internal class FakePaymentsRepository() : PaymentsRepository {

    override suspend fun createPaymentIntent(
        signedOffer: String,
    ): Either<Throwable, PaymentIntent> = runCatching {
        PaymentIntent(
            paymentId = "ex",
            paymentIntentId = "dolores",
            accountId = "sanctus",
            clientSecret = "percipit",
            amount = 10.11,
            currency = "consetetur"
        )
    }.toEither()

    override suspend fun authorizeSession(paymentIntentId: String): Either<Throwable, String> {
        return runCatching {
            ""
        }.toEither()

    }

    override suspend fun getPublishableKey(): Either<Throwable, String> {
        return runCatching {
            ""
        }.toEither()
    }

    override suspend fun getPaymentSummary(paymentId: String): Either<Throwable, PaymentSummary> {
        return runCatching {
            PaymentSummary(
                evseId = "nulla",
                cpoName = "Wanda Dodson",
                address = "litora",
                totalTime = 7412,
                consumedKWh = 14.15,
                totalCost = 2803
            )
        }.toEither()
    }
}

