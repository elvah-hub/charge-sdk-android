package de.elvah.charge.features.adhoc_charging.data.service

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.domain.model.ChargeSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeServiceState
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargingSessionState
import de.elvah.charge.features.adhoc_charging.domain.service.charge.errors.ChargeError
import de.elvah.charge.features.adhoc_charging.domain.service.charge.extension.isSessionRunning
import de.elvah.charge.features.adhoc_charging.domain.service.charge.extension.isSummaryReady
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class ElvahChargeService(
    lifecycle: Lifecycle?,
    private val chargingRepository: ChargingRepository,
    private val getPaymentSummary: GetPaymentSummary,
    private val chargeScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    checkOnInit: Boolean = true,
) : ChargeService, DefaultLifecycleObserver {

    private val _state = MutableStateFlow(ChargeServiceState.IDLE)
    override val state: StateFlow<ChargeServiceState> = _state

    private val _chargeSession = MutableStateFlow<ChargeSession?>(null)
    override val chargeSession: StateFlow<ChargeSession?> = _chargeSession

    override val chargeSessionState = combine(
        state,
        chargeSession,
    ) { state, session ->
        buildChargingSessionState(
            isSessionRunning = session?.status?.isSessionRunning == true,
            isSessionSummaryReady = state.isSummaryReady,
            lastSessionData = session,
        )

    }.stateIn(
        scope = chargeScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = buildChargingSessionState(),
    )

    private val _errors = MutableStateFlow<ChargeError?>(null)
    override val errors: StateFlow<ChargeError?> = _errors

    private var paymentSummary: PaymentSummary? = null
    internal var isPolling = false
    internal var pollingJob: Job? = null

    init {
        lifecycle?.addObserver(this)

        if (checkOnInit) {
            checkForActiveSession()
        }
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        if (isPolling) {
            setupPollingJob()
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        if (isPolling) {
            pollingJob?.cancel()
            pollingJob = null
        }
    }

    override fun startSession() {
        if (_state.value == ChargeServiceState.STARTING) return
        setChargeError(null)
        _state.value = ChargeServiceState.STARTING

        chargeScope.launch {
            chargingRepository.startChargingSession()
                .also {
                    when (it) {
                        is Either.Left -> {
                            setChargeError(ChargeError.StartAttemptFailed(it.value))
                            _state.value = ChargeServiceState.IDLE
                        }

                        is Either.Right -> {
                            _state.value = ChargeServiceState.STARTED
                            startPolling()
                        }
                    }
                }
        }
    }

    override fun stopSession() {
        if (_state.value == ChargeServiceState.STOPPING) return
        setChargeError(null)
        val prevState = _state.value
        _state.value = ChargeServiceState.STOPPING

        chargeScope.launch {
            chargingRepository.stopChargingSession()
                .also {
                    when (it) {
                        is Either.Left -> {
                            setChargeError(ChargeError.StopAttemptFailed(it.value))
                            _state.value = prevState
                        }

                        is Either.Right -> {
                            onSessionStopped()
                        }
                    }
                }
        }
    }

    private fun onSessionStopped() {
        stopPolling()
        _state.value = ChargeServiceState.SUMMARY
    }

    override fun checkForActiveSession() {
        if (_state.value == ChargeServiceState.VERIFYING) return
        setChargeError(null)
        _state.value = ChargeServiceState.VERIFYING

        chargeScope.launch {
            chargingRepository
                .fetchChargingSession()
                .fold(
                    ifLeft = {
                        // TODO: retry options? for first failed call
                        setChargeError(ChargeError.SessionCheckFailed(it))
                        _state.value = ChargeServiceState.IDLE
                    },
                    ifRight = {
                        if (it.status == SessionStatus.STOPPED) {
                            when {
                                chargingRepository.getSummary() != null -> onSessionStopped()
                                else -> reset()
                            }

                        } else {
                            _state.value = ChargeServiceState.STARTED
                            startPolling()
                        }
                    }
                )
        }
    }

    override fun reset() {
        chargeScope.launch {
            chargingRepository.resetSession()

            paymentSummary = null
            isPolling = false
            pollingJob?.cancel()
            pollingJob = null

            setChargeError(null)
            _chargeSession.tryEmit(null)
            _state.value = ChargeServiceState.IDLE
        }
    }

    override suspend fun getSummary(): PaymentSummary? {
        if (_state.value != ChargeServiceState.SUMMARY) return null

        if (paymentSummary != null) return paymentSummary
        val chargeSummary = chargingRepository.getSummary()

        if (chargeSummary == null) {
            reset()
            return null
        }

        setChargeError(null)

        var retryAttempt = CHARGE_SESSION_SUMMARY_MAX_RETRY
        delay(CHARGE_SESSION_SUMMARY_DELAY)

        while (retryAttempt > 0) {
            getPaymentSummary(paymentId = chargeSummary.paymentId).fold(
                ifLeft = {
                    retryAttempt--

                    if (retryAttempt <= 0) {
                        setChargeError(ChargeError.SummaryRetryFailedAllAttempts)
                    } else {
                        setChargeError(ChargeError.SummaryRetryFailed(it))
                        delay(CHARGE_SESSION_SUMMARY_RETRY_DELAY)
                    }
                },
                ifRight = {
                    retryAttempt = 0
                    paymentSummary = it
                }
            )
        }

        return paymentSummary
    }

    private fun startPolling() {
        if (isPolling) return
        isPolling = true

        setupPollingJob()
    }

    private fun stopPolling() {
        if (!isPolling) return
        isPolling = false

        pollingJob?.cancel()
        pollingJob = null
    }

    private fun setupPollingJob() {
        pollingJob?.cancel()
        pollingJob = chargeScope.launch {
            while (isPolling) {
                chargingRepository
                    .fetchChargingSession()
                    .fold(
                        ifLeft = {
                            // TODO: depending on error stop the polling
                            setChargeError(ChargeError.SessionPollingFailed(it))
                        },
                        ifRight = { session ->
                            setChargeError(null)
                            _chargeSession.tryEmit(session)

                            if (session.status == SessionStatus.STOPPED) {
                                onSessionStopped()
                            }
                        }
                    )

                delay(CHARGE_SESSION_POLLING_INTERVAL)
            }
        }
    }

    private fun buildChargingSessionState(
        isSessionRunning: Boolean = chargeSession.value?.status?.isSessionRunning == true,
        isSessionSummaryReady: Boolean = state.value.isSummaryReady,
        lastSessionData: ChargeSession? = chargeSession.value,
    ): ChargingSessionState {
        return ChargingSessionState(
            isSessionRunning = isSessionRunning,
            isSessionSummaryReady = isSessionSummaryReady,
            lastSessionData = lastSessionData,
        )
    }

    private fun setChargeError(error: ChargeError?) {
        _errors.value = error
    }

    companion object {

        private val CHARGE_SESSION_POLLING_INTERVAL = 1.seconds
        private const val CHARGE_SESSION_SUMMARY_MAX_RETRY = 3
        private val CHARGE_SESSION_SUMMARY_DELAY = 500.milliseconds
        private val CHARGE_SESSION_SUMMARY_RETRY_DELAY = 2.seconds
    }
}
