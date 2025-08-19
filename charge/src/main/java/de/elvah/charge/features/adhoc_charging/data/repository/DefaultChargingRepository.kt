package de.elvah.charge.features.adhoc_charging.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.adhoc_charging.data.mapper.toDomain
import de.elvah.charge.features.adhoc_charging.data.remote.api.ChargingApi
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.platform.core.arrow.extensions.toEither
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


internal class DefaultChargingRepository(
    private val chargingApi: ChargingApi,
    private val chargingStore: ChargingStore
) : ChargingRepository {

    private val _activeSessions: MutableSharedFlow<ChargingSession?> = MutableSharedFlow(replay = 1)
    override val activeSessions: Flow<ChargingSession?>
        get() = _activeSessions.asSharedFlow()

    override suspend fun updateChargingToken(token: String) {
        chargingStore.setToken(token)
    }

    override suspend fun updateOrganisationDetails(organisationDetails: OrganisationDetails) {
        chargingStore.saveOrganisationDetails(organisationDetails)
    }

    override suspend fun fetchChargingSession(): Either<Throwable, ChargingSession> {
        val session = runCatching {
            chargingApi.getActiveChargeSessions(
                BEARER_TEMPLATE.format(
                    getToken()
                )
            )
        }.map { it.toDomain() }
        _activeSessions.emit(session.getOrNull())
        return session.toEither()
    }

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

    private fun getToken() = runBlocking {
        chargingStore.getChargingPrefs().first().token
    }
}

private const val BEARER_TEMPLATE = "Bearer %s"
