package de.elvah.charge.features.payments.domain.repository

import arrow.core.Either
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.PaymentIntent
import de.elvah.charge.features.payments.domain.model.PaymentSummary

internal interface PaymentsRepository {

    val organisationDetails: OrganisationDetails?

    suspend fun createPaymentIntent(signedOffer: String): Either<Exception, PaymentIntent>

    suspend fun authorizeSession(paymentIntentId: String): Either<Exception, String>

    suspend fun getPublishableKey(): Either<Exception, String>

    suspend fun getPaymentSummary(paymentId: String): Either<Exception, PaymentSummary>
}