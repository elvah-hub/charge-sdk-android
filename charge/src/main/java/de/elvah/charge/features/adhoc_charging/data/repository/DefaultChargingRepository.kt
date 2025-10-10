package de.elvah.charge.features.adhoc_charging.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.adhoc_charging.data.mapper.toDomain
import de.elvah.charge.features.adhoc_charging.data.remote.api.ChargingApi
import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SummaryInfo
import de.elvah.charge.features.sites.domain.model.AdditionalCosts
import de.elvah.charge.platform.core.arrow.extensions.toEither
import kotlinx.coroutines.flow.first

internal class DefaultChargingRepository(
    private val chargingApi: ChargingApi,
    private val chargingStore: ChargingStore
) : ChargingRepository {

    override suspend fun startChargingSession(): Either<SessionExceptions, Boolean> {
        return runCatching {
            chargingApi.startChargeSessions(BEARER_TEMPLATE.format(getToken()))
        }.fold(
            onSuccess = {
                true.right()
            }, onFailure = {
                when (it) {
                    is IllegalStateException -> SessionExceptions.OngoingSession.left()
                    else -> SessionExceptions.GenericError.left()
                }
            }
        )
    }

    override suspend fun stopChargingSession(): Either<SessionExceptions, Boolean> {
        return runCatching {
            chargingApi.stopChargeSession(BEARER_TEMPLATE.format(getToken()))
        }.fold(
            onSuccess = {
                true.right()
            }, onFailure = {
                SessionExceptions.GenericError.left()
            }
        )
    }

    override suspend fun fetchChargingSession(): Either<Throwable, ChargeSession> {
        val token = getToken()

        return if (token.isNotEmpty()) {
            runCatching {
                chargingApi.getActiveChargeSessions(BEARER_TEMPLATE.format(token))
            }
                .map { it.toDomain() }
                .toEither()
                .also { it }
        } else {
            Either.Left(IllegalStateException("No token found"))
        }
    }

    override suspend fun getSummary(): SummaryInfo? {
        return chargingStore.getChargingPrefs().first()
            .takeIf { it.paymentId.isNotEmpty() }
            ?.takeIf { it.logoUrl.isNotEmpty() }
            ?.let {
                SummaryInfo(
                    paymentId = it.paymentId,
                    logoUrl = it.logoUrl,
                )
            }
    }

    override suspend fun resetSession() {
        chargingStore.resetSession()
    }

    override suspend fun updateChargingToken(token: String) {
        chargingStore.setToken(token)
    }

    override suspend fun updateOrganisationDetails(organisationDetails: OrganisationDetails) {
        chargingStore.saveOrganisationDetails(organisationDetails)
    }

    override suspend fun getAdditionalCosts(): AdditionalCosts? {
        return chargingStore.getAdditionalCosts()
    }

    override suspend fun storeAdditionalCosts(additionalCosts: AdditionalCosts?) {
        chargingStore.storeAdditionalCosts(additionalCosts)
    }

    private suspend fun getToken() = chargingStore.getChargingPrefs().first().token

    companion object {
        private const val BEARER_TEMPLATE = "Bearer %s"
    }
}
