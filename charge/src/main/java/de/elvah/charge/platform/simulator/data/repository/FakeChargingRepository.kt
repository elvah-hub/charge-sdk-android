package de.elvah.charge.platform.simulator.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.update


internal class FakeChargingRepository(
    private val chargingStore: ChargingStore
) : ChargingRepository {

    private val _activeSessions: MutableSharedFlow<ChargingSession?> = MutableSharedFlow()
    override val activeSessions: Flow<ChargingSession?>
        get() = _activeSessions.asSharedFlow()

    private val currentActiveSession = MutableStateFlow<ChargingSession?>(null)

    private var sessionCounter = 0
    private var secondsSinceLastChange = 0
    private var bannerRequested = false
    private var stopRequested = false

    override suspend fun updateChargingToken(token: String) {
        chargingStore.setToken(token)
    }

    override suspend fun updateOrganisationDetails(organisationDetails: OrganisationDetails) {
        chargingStore.saveOrganisationDetails(organisationDetails)
    }

    override suspend fun fetchChargingSession(): Either<Exception, ChargingSession> {
        val session1 = generate()
        val session = session1?.right() ?: NullPointerException().left()

        return session.also {
            _activeSessions.emit(session.getOrNull())
        }.also {
            sessionCounter++
        }.also {
            currentActiveSession.update {
                session.getOrNull()
            }
        }
    }

    override suspend fun startChargingSession(): Either<SessionExceptions, Boolean> {
        val simulatorFlow = (ChargeConfig.config.environment as Environment.Simulator).simulatorFlow
        return if (simulatorFlow !is SimulatorFlow.StartFails) true.right() else SessionExceptions.GenericError.left()
    }

    override suspend fun stopChargingSession(): Either<SessionExceptions, Boolean> {
        stopRequested = true
        val simulatorFlow = (ChargeConfig.config.environment as Environment.Simulator).simulatorFlow
        return if (simulatorFlow !is SimulatorFlow.StopFails) true.right() else SessionExceptions.GenericError.left()

    }

    private fun generate(): ChargingSession? {
        if (!bannerRequested) {
            bannerRequested = true
            return null
        }
        val simulatorFlow = (ChargeConfig.config.environment as Environment.Simulator).simulatorFlow
        return when (simulatorFlow) {
            SimulatorFlow.Default -> generateDefaultActiveSession()
            SimulatorFlow.StartFails -> generateStartFailsActiveSession()
            SimulatorFlow.StopFails -> generateStopFailsActiveSession()
            SimulatorFlow.InterruptedCharge -> generateInterruptedChargeActiveSession()
            SimulatorFlow.SlowDefault -> TODO()
            SimulatorFlow.StartRejected -> generateStartRejectedActiveSession()
            SimulatorFlow.StatusMissing -> TODO()
            SimulatorFlow.StopRejected -> generateStartRejectedActiveSession()
        }
    }

    private fun generateDefaultActiveSession(): ChargingSession? {
        return when (currentActiveSession.value?.status1) {
            SessionStatus.START_REQUESTED -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = SessionStatus.STARTED.name,
                    consumption = Math.random() + sessionCounter,
                    duration = sessionCounter * 3,
                    status1 = SessionStatus.STARTED
                )
            }

            SessionStatus.STARTED -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = 0.0,
                    duration = 0,
                    status1 = SessionStatus.CHARGING
                )
            }

            SessionStatus.START_REJECTED -> {
                currentActiveSession.value?.incrementDuration()
            }

            SessionStatus.CHARGING -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = Math.random() + sessionCounter,
                    duration = sessionCounter * 3,
                    status1 = SessionStatus.CHARGING
                )
            }

            SessionStatus.STOP_REQUESTED -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = Math.random() + sessionCounter,
                    duration = sessionCounter * 3,
                    status1 = SessionStatus.STOPPED
                )
            }

            SessionStatus.STOPPED -> {
                currentActiveSession.value?.incrementDuration()
            }

            else -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = Math.random() + sessionCounter,
                    duration = sessionCounter * 3,
                    status1 = SessionStatus.START_REQUESTED
                )
            }
        }
    }

    private fun generateStartFailsActiveSession(): ChargingSession? {
        return when (currentActiveSession.value?.status1) {
            null -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = 0.0,
                    duration = 0,
                    status1 = SessionStatus.START_REQUESTED
                )
            }

            SessionStatus.START_REQUESTED,
            SessionStatus.START_REJECTED -> {
                if (sessionCounter > 3) {
                    ChargingSession(
                        evseId = "DE*KDL*E0000040",
                        status = "auctor",
                        consumption = currentActiveSession.value?.consumption ?: 0.0,
                        duration = sessionCounter,
                        status1 = SessionStatus.START_REJECTED
                    )
                } else {
                    currentActiveSession.value
                }
            }

            else -> {
                currentActiveSession.value
            }
        }
    }

    private fun generateStopFailsActiveSession(): ChargingSession? {
        return when (currentActiveSession.value?.status1) {
            null -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = 0.0,
                    duration = 0,
                    status1 = SessionStatus.START_REQUESTED
                )
            }

            SessionStatus.START_REQUESTED -> {
                secondsSinceLastChange = 0
                if (sessionCounter > 2) {
                    ChargingSession(
                        evseId = "DE*KDL*E0000040",
                        status = "auctor",
                        consumption = currentActiveSession.value?.consumption ?: 0.0,
                        duration = sessionCounter,
                        status1 = SessionStatus.STARTED
                    )
                } else {
                    currentActiveSession.value
                }
            }

            SessionStatus.STARTED -> {
                secondsSinceLastChange = 0
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = 0.0,
                    duration = 0,
                    status1 = SessionStatus.CHARGING
                )
            }

            SessionStatus.CHARGING -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = Math.random() + secondsSinceLastChange,
                    duration = secondsSinceLastChange * 3,
                    status1 = SessionStatus.CHARGING
                )
            }

            SessionStatus.STOP_REQUESTED -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = Math.random() + sessionCounter,
                    duration = sessionCounter * 3,
                    status1 = SessionStatus.STOP_REJECTED
                )
            }

            else -> {
                currentActiveSession.value
            }
        }.also {
            secondsSinceLastChange++
        }
    }

    private fun generateInterruptedChargeActiveSession(): ChargingSession? {
        return when (currentActiveSession.value?.status1) {
            null -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = 0.0,
                    duration = 0,
                    status1 = SessionStatus.START_REQUESTED
                )
            }

            SessionStatus.START_REQUESTED -> {
                secondsSinceLastChange = 0
                if (sessionCounter > 2) {
                    ChargingSession(
                        evseId = "DE*KDL*E0000040",
                        status = "auctor",
                        consumption = currentActiveSession.value?.consumption ?: 0.0,
                        duration = sessionCounter,
                        status1 = SessionStatus.STARTED
                    )
                } else {
                    currentActiveSession.value
                }
            }

            SessionStatus.STARTED -> {
                secondsSinceLastChange = 0
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = 0.0,
                    duration = 0,
                    status1 = SessionStatus.CHARGING
                )
            }

            SessionStatus.CHARGING -> {
                if (secondsSinceLastChange < 8) {
                    ChargingSession(
                        evseId = "DE*KDL*E0000040",
                        status = "auctor",
                        consumption = Math.random() + secondsSinceLastChange,
                        duration = secondsSinceLastChange * 3,
                        status1 = SessionStatus.CHARGING
                    )
                } else {
                    ChargingSession(
                        evseId = "DE*KDL*E0000040",
                        status = "auctor",
                        consumption = Math.random() + secondsSinceLastChange,
                        duration = secondsSinceLastChange * 3,
                        status1 = SessionStatus.STOP_REQUESTED
                    )
                }
            }

            SessionStatus.STOP_REQUESTED -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = Math.random() + sessionCounter,
                    duration = sessionCounter * 3,
                    status1 = SessionStatus.STOP_REJECTED
                )
            }

            else -> {
                currentActiveSession.value
            }
        }.also {
            secondsSinceLastChange++
        }
    }

    private fun generateStartRejectedActiveSession(): ChargingSession? {
        return when (currentActiveSession.value?.status1) {
            null -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = 0.0,
                    duration = 0,
                    status1 = SessionStatus.START_REQUESTED
                )
            }

            SessionStatus.START_REQUESTED -> {
                secondsSinceLastChange = 0
                if (sessionCounter > 2) {
                    ChargingSession(
                        evseId = "DE*KDL*E0000040",
                        status = "auctor",
                        consumption = currentActiveSession.value?.consumption ?: 0.0,
                        duration = sessionCounter,
                        status1 = SessionStatus.START_REJECTED
                    )
                } else {
                    currentActiveSession.value
                }
            }

            else -> {
                currentActiveSession.value
            }
        }.also {
            secondsSinceLastChange++
        }
    }

    private fun generateStopRejectedActiveSession(): ChargingSession? {
        return when (currentActiveSession.value?.status1) {
            null -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = 0.0,
                    duration = 0,
                    status1 = SessionStatus.START_REQUESTED
                )
            }

            SessionStatus.START_REQUESTED -> {
                secondsSinceLastChange = 0
                if (sessionCounter > 2) {
                    ChargingSession(
                        evseId = "DE*KDL*E0000040",
                        status = "auctor",
                        consumption = currentActiveSession.value?.consumption ?: 0.0,
                        duration = sessionCounter,
                        status1 = SessionStatus.STARTED
                    )
                } else {
                    currentActiveSession.value
                }
            }

            SessionStatus.STARTED -> {
                secondsSinceLastChange = 0
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = 0.0,
                    duration = 0,
                    status1 = SessionStatus.CHARGING
                )
            }

            SessionStatus.CHARGING -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = Math.random() + secondsSinceLastChange,
                    duration = secondsSinceLastChange * 3,
                    status1 = SessionStatus.CHARGING
                )
            }

            SessionStatus.STOP_REQUESTED -> {
                ChargingSession(
                    evseId = "DE*KDL*E0000040",
                    status = "auctor",
                    consumption = Math.random() + sessionCounter,
                    duration = sessionCounter * 3,
                    status1 = SessionStatus.STOP_REJECTED
                )
            }

            else -> {
                currentActiveSession.value
            }
        }.also {
            secondsSinceLastChange++
        }
    }

    private fun ChargingSession?.incrementDuration() = this?.let {
        ChargingSession(
            evseId = this.evseId,
            status = this.status,
            consumption = this.consumption,
            duration = this.duration + 3,
            status1 = this.status1
        )
    }
}


enum class SessionStatus {
    START_REQUESTED, STARTED, START_REJECTED, CHARGING, STOPPED, STOP_REQUESTED, STOP_REJECTED
}

