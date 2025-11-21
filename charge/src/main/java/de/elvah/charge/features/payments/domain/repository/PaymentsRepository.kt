package de.elvah.charge.features.payments.domain.repository

import arrow.core.Either
import de.elvah.charge.features.payments.domain.model.PaymentIntent
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import de.elvah.charge.features.payments.domain.model.PublishableKey

internal interface PaymentsRepository {

    suspend fun createPaymentIntent(signedOffer: String): Either<Throwable, PaymentIntent>

    suspend fun authorizeSession(paymentIntentId: String): Either<Throwable, String>

    suspend fun getPublishableKey(): Either<Throwable, PublishableKey>

    suspend fun getPaymentSummary(paymentId: String): Either<Throwable, PaymentSummary>
}
