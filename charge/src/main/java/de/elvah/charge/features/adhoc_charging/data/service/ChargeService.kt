package de.elvah.charge.features.adhoc_charging.data.service

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration.Companion.seconds

internal interface ChargeService {

    val activeSession: Flow<ChargingSession?>

    suspend fun startChargingSession(): Either<SessionExceptions, Boolean>

    suspend fun stopChargingSession(): Either<SessionExceptions, Boolean>

    suspend fun resetSession()
}

internal class ElvahChargeService(
    private val chargingRepository: ChargingRepository,
) : ChargeService {

    override val activeSession: Flow<ChargingSession?>
        get() = flow {
            val result = chargingRepository.fetchChargingSession()

            // check status
            // check error if there is an active session or not

            while (true) {
                result.fold(
                    ifLeft = { error ->
                        // TODO: check if error is charge session stopped if not, ignore and keep polling
                        emit(null)
                        return@flow
                    },
                    ifRight = { session ->
                        session.status
"CHARGING"
                        emit(session)
                        delay(1.seconds)
                    }
                )
            }
        }

    private val _activeSessions: MutableSharedFlow<ChargingSession?> = MutableSharedFlow(replay = 1)
    val activeSessions: Flow<ChargingSession?>
        get() = flow {
            emit(chargingRepository.fetchChargingSession().getOrNull())
        }

    override suspend fun startChargingSession(): Either<SessionExceptions, Boolean> =
        chargingRepository.startChargingSession()

    override suspend fun stopChargingSession(): Either<SessionExceptions, Boolean> =
        chargingRepository.stopChargingSession()

    override suspend fun resetSession() {
        chargingRepository.resetSession()
        _activeSessions.tryEmit(null)
    }
}
