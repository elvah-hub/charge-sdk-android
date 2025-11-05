package de.elvah.charge.public_api.manager

import android.content.Context
import de.elvah.charge.features.adhoc_charging.domain.service.charge.extension.isSessionRunning
import de.elvah.charge.features.adhoc_charging.domain.service.charge.extension.isSummaryReady
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargeServiceState
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import de.elvah.charge.features.sites.ui.utils.goToChargingSession
import de.elvah.charge.public_api.mapper.toPublic
import de.elvah.charge.public_api.model.ChargeSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.java.KoinJavaComponent

public object ChargeSessionManager {

    private val observeChargingSession: ObserveChargingSession by KoinJavaComponent.inject(
        ObserveChargingSession::class.java
    )
    private val observeState: ObserveChargeServiceState by KoinJavaComponent.inject(
        ObserveChargeServiceState::class.java
    )

    private val stopChargingSession: StopChargingSession by KoinJavaComponent.inject(
        StopChargingSession::class.java
    )

    public val isSummaryReady: Flow<Boolean> = observeState().map { it.isSummaryReady }

    public val hasActiveSession: Flow<Boolean> =
        observeChargingSession().map { it?.status?.isSessionRunning == true }

    public val chargeSession: Flow<ChargeSession?> = observeChargingSession()
        .map { it?.toPublic() }

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
