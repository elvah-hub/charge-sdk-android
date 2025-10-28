package de.elvah.charge.features.adhoc_charging.domain.service.charge

import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.adhoc_charging.domain.service.charge.errors.ChargeError
import de.elvah.charge.features.payments.domain.model.PaymentSummary
import kotlinx.coroutines.flow.StateFlow

internal interface ChargeService {

    val state: StateFlow<ChargeState>

    val chargeSession: StateFlow<ChargingSession?>

    val errors: StateFlow<ChargeError?>

    fun startSession()

    fun stopSession()

    fun checkForActiveSession()

    fun reset()

    suspend fun getSummary(): PaymentSummary?
}
