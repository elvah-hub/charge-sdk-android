package de.elvah.charge.features.adhoc_charging.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.adhoc_charging.data.mapper.toDomain
import de.elvah.charge.features.adhoc_charging.data.remote.api.ChargingApi
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.platform.core.arrow.extensions.toEither
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow



internal class DefaultChargingRepository(
    private val chargingApi: ChargingApi,
    private val chargingStore: ChargingStore
) : ChargingRepository {

    private val _activeSessions: MutableSharedFlow<ChargingSession?> = MutableSharedFlow()
    override val activeSessions: Flow<ChargingSession?>
        get() = _activeSessions.asSharedFlow()

    override fun updateChargingToken(token: String) {
        chargingStore.setToken(token)
    }

    override suspend fun fetchChargingSession(): Either<Exception, ChargingSession> {
        val session = runCatching {
            chargingApi.getActiveChargeSessions(BEARER_TEMPLATE.format(chargingStore.getToken().orEmpty()))
        }.map { it.toDomain() }
        _activeSessions.emit(session.getOrNull())
        return session.toEither()
    }

    override suspend fun startChargingSession() {
        runCatching {
            chargingApi.startChargeSessions(BEARER_TEMPLATE.format(chargingStore.getToken().orEmpty()))
        }
    }

    override suspend fun stopChargingSession() {
        runCatching {
            chargingApi.stopChargeSession(BEARER_TEMPLATE.format(chargingStore.getToken().orEmpty()))
        }
    }
}

private const val BEARER_TEMPLATE = "Bearer %s"