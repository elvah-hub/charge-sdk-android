package de.elvah.charge.platform.simulator.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow


internal class FakeChargingRepository(
    private val chargingStore: ChargingStore
) : ChargingRepository {

    private val _activeSessions: MutableSharedFlow<ChargingSession?> = MutableSharedFlow()
    override val activeSessions: Flow<ChargingSession?>
        get() = _activeSessions.asSharedFlow()

    private var sessionCounter = 0

    override suspend fun updateChargingToken(token: String) {
        chargingStore.setToken(token)
    }

    override suspend fun updateOrganisationDetails(organisationDetails: OrganisationDetails) {
        chargingStore.saveOrganisationDetails(organisationDetails)
    }

    override suspend fun fetchChargingSession(): Either<Exception, ChargingSession> {
        val session = if (sessionCounter == 0) {
            NullPointerException().left()
        } else {
            val randomComsumption = Math.random() + sessionCounter

            ChargingSession(
                evseId = "graeci",
                status = "auctor",
                consumption = randomComsumption,
                duration = sessionCounter * 3
            ).right()
        }
        return session.also {
            _activeSessions.emit(session.getOrNull())
        }.also {
            sessionCounter++
        }
    }

    override suspend fun startChargingSession(): Either<SessionExceptions, Boolean> {
        return true.right()
    }

    override suspend fun stopChargingSession() {
        runCatching {
            //No-op
        }
    }
}

