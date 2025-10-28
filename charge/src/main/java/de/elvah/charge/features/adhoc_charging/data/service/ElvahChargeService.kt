package de.elvah.charge.features.adhoc_charging.data.service

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeService
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargeState
import de.elvah.charge.features.adhoc_charging.domain.service.charge.errors.ChargeError
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

internal class ElvahChargeService(
    lifecycle: Lifecycle,
    private val chargingRepository: ChargingRepository,
    private val getPaymentSummary: GetPaymentSummary,
    private val chargeScope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
    checkOnInit: Boolean = true,
) : ChargeService, DefaultLifecycleObserver {

    private val _state = MutableStateFlow(ChargeState.IDLE)
    override val state: StateFlow<ChargeState> = _state

    private val _chargeSession = MutableStateFlow<ChargingSession?>(null)
    override val chargeSession: StateFlow<ChargingSession?> = _chargeSession

    private val _errors = MutableStateFlow<ChargeError?>(null)
    override val errors: StateFlow<ChargeError?> = _errors

    private var paymentSummary: PaymentSummary? = null
    internal var isPolling = false
    internal var pollingJob: Job? = null

    init {
        lifecycle.addObserver(this)

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
        if (_state.value == ChargeState.STARTING) return
        _state.value = ChargeState.STARTING

        chargeScope.launch {
            chargingRepository.startChargingSession()
                .also {
                    when (it) {
                        is Either.Left -> {
                            _errors.value = it.value.toChargeError()
                            _state.value = ChargeState.IDLE
                        }

                        is Either.Right -> {
                            _state.value = ChargeState.STARTED
                            startPolling()
                        }
                    }
                }
        }
    }

    override fun stopSession() {
        if (_state.value == ChargeState.STOPPING) return
        val prevState = _state.value
        _state.value = ChargeState.STOPPING

        chargeScope.launch {
            chargingRepository.stopChargingSession()
                .also {
                    when (it) {
                        is Either.Left -> {
                            _errors.value = it.value.toChargeError()
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
        _state.value = ChargeState.SUMMARY
    }

    override fun checkForActiveSession() {
        if (_state.value == ChargeState.VERIFYING) return
        _state.value = ChargeState.VERIFYING

        chargeScope.launch {
            chargingRepository
                .fetchChargingSession()
                .fold(
                    ifLeft = {
                        // TODO: retry options? for first failed call
                        _errors.value = it.toChargeError()
                        _state.value = ChargeState.IDLE
                    },
                    ifRight = {
                        if (it.status == SessionStatus.STOPPED) {
                            when {
                                chargingRepository.getSummary() != null -> onSessionStopped()
                                else -> reset()
                            }

                        } else {
                            _state.value = ChargeState.STARTED
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

            _state.value = ChargeState.IDLE
        }
    }

    override suspend fun getSummary(): PaymentSummary? {
        if (_state.value != ChargeState.SUMMARY) return null

        if (paymentSummary != null) return paymentSummary
        val chargeSummary = chargingRepository.getSummary()

        if (chargeSummary == null) {
            reset()
            return null
        }

        var retryAttempt = CHARGE_SESSION_SUMMARY_MAX_RETRY
        delay(CHARGE_SESSION_SUMMARY_DELAY)

        while (retryAttempt > 0) {
            getPaymentSummary(paymentId = chargeSummary.paymentId.orEmpty()).fold(
                ifLeft = {
                    retryAttempt.minus(1)

                    if (retryAttempt <= 0) {
                        _errors.value = ChargeError.SUMMARY_FAILED_ALL_ATTEMPTS
                    } else {
                        _errors.value = it.toChargeError()
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
                            _errors.value = it.toChargeError()
                        },
                        ifRight = { session ->
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

    companion object {

        private val CHARGE_SESSION_POLLING_INTERVAL = 1.seconds
        private const val CHARGE_SESSION_SUMMARY_MAX_RETRY = 3
        private val CHARGE_SESSION_SUMMARY_DELAY = 500.milliseconds
        private val CHARGE_SESSION_SUMMARY_RETRY_DELAY = 2.seconds
    }
}
