package de.elvah.charge.features.payments.data.repository

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.data.mapper.toDomain
import de.elvah.charge.features.payments.data.remote.api.ChargeSettlementApi
import de.elvah.charge.features.payments.data.remote.api.IntegrateApi
import de.elvah.charge.features.payments.data.remote.model.request.AuthorizeSessionRequest
import de.elvah.charge.features.payments.data.remote.model.request.CreatePaymentIntentRequest
import de.elvah.charge.features.payments.domain.model.PaymentIntent
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.platform.core.arrow.extensions.toEither
import kotlinx.coroutines.flow.first


internal class DefaultPaymentsRepository(
    private val chargeSettlementApi: ChargeSettlementApi,
    private val integrateApi: IntegrateApi,
    private val chargingStore: ChargingStore
) : PaymentsRepository {

    override suspend fun createPaymentIntent(
        signedOffer: String,
    ): Either<Throwable, PaymentIntent> = runCatching {
        val response =
            chargeSettlementApi.createPaymentIntent(CreatePaymentIntentRequest(signedOffer))

        chargingStore.saveOrganisationDetails(response.data.organisationDetails.toDomain())

        response.toDomain()
    }.toEither()

    override suspend fun authorizeSession(paymentIntentId: String): Either<Throwable, String> {
        return runCatching {
            chargeSettlementApi.authorizeSession(AuthorizeSessionRequest(paymentIntentId)).data.chargeIdentityToken
        }.toEither()
    }

    override suspend fun getPublishableKey(): Either<Throwable, String> {
        return runCatching {
            integrateApi.getPublishableKey().data.publishableKey
        }.toEither()
    }

    override suspend fun getPaymentSummary(paymentId: String): Either<Throwable, PaymentSummary> {
        return runCatching {
            val sessionDetails = chargingStore.getChargingPrefs().first()

            chargeSettlementApi
                .getPaymentSummary(paymentId)
                .toDomain(
                    evseId = sessionDetails.evseId,
                    cpoName = sessionDetails.cpoName,
                    logoUrl = sessionDetails.logoUrl,
                )
        }.toEither()
    }
}

