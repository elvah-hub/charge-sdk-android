package de.elvah.charge.public_api.manager

import android.content.Context
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargeSessionState
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import de.elvah.charge.features.sites.ui.utils.goToChargingSession
import de.elvah.charge.public_api.mapper.toPublic
import de.elvah.charge.public_api.model.ChargeSession
import de.elvah.charge.public_api.model.ChargingSessionState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.java.KoinJavaComponent

public object ChargeSessionManager {

    private val observeChargeSession: ObserveChargingSession by KoinJavaComponent.inject(
        ObserveChargingSession::class.java
    )

    private val observeChargeSessionState: ObserveChargeSessionState by KoinJavaComponent.inject(
        ObserveChargeSessionState::class.java
    )

    private val stopChargingSession: StopChargingSession by KoinJavaComponent.inject(
        StopChargingSession::class.java
    )

    public val chargeSession: Flow<ChargeSession?> = observeChargeSession()
        .map { it?.toPublic() }

    public val chargingSessionState: Flow<ChargingSessionState> = observeChargeSessionState()
        .map { it.toPublic() }

    public fun openSession(context: Context) {
        context.goToChargingSession(false)
    }

    public fun openSessionSummary(context: Context) {
        context.goToChargingSession(true)
    }

    public fun stopSession() {
        stopChargingSession()
    }
}
