package de.elvah.charge.features.payments.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.payments.data.mapper.toDomain
import de.elvah.charge.features.payments.data.remote.api.ChargeSettlementApi
import de.elvah.charge.features.payments.data.remote.api.IntegrateApi
import de.elvah.charge.features.payments.data.remote.model.request.AuthorizeSessionRequest
import de.elvah.charge.features.payments.data.remote.model.request.CreatePaymentIntentRequest
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.PaymentIntent
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.platform.core.arrow.extensions.toEither



internal class DefaultPaymentsRepository(
    private val chargeSettlementApi: ChargeSettlementApi,
    private val integrateApi: IntegrateApi,
) : PaymentsRepository {

    private var _organisationDetails: OrganisationDetails? = null
    override val organisationDetails: OrganisationDetails?
        get() = _organisationDetails

    override suspend fun createPaymentIntent(
        signedOffer: String,
    ): Either<Exception, PaymentIntent> = runCatching {
        val response =
            chargeSettlementApi.createPaymentIntent(CreatePaymentIntentRequest(signedOffer))
        _organisationDetails = response.data.organisationDetails.toDomain()

        response.toDomain()
    }.toEither()

    override suspend fun authorizeSession(paymentIntentId: String): Either<Exception, String> {
        return runCatching {
            chargeSettlementApi.authorizeSession(AuthorizeSessionRequest(paymentIntentId)).data.chargeIdentityToken
        }.toEither()

    }

    override suspend fun getPublishableKey(): Either<Exception, String> {
        return runCatching {
            integrateApi.getPublishableKey().data.publishableKey
        }.toEither()
    }

    override suspend fun getPaymentSummary(paymentId: String): Either<Exception, PaymentSummary> {
        return runCatching {
            chargeSettlementApi.getPaymentSummary(paymentId)
                .toDomain(organisationDetails?.companyName.orEmpty())
        }.toEither()
    }
}

