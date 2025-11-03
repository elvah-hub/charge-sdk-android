package de.elvah.charge.public_api.session

import android.content.Context
import de.elvah.charge.features.adhoc_charging.domain.service.charge.extension.isSummaryState
import de.elvah.charge.features.adhoc_charging.domain.usecase.GetChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.HasActiveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingState
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import de.elvah.charge.features.sites.ui.utils.goToChargingSession
import de.elvah.charge.public_api.session.model.ChargeSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.java.KoinJavaComponent

public object SessionManager {

    private val hasActiveChargingSession: HasActiveChargingSession by KoinJavaComponent.inject(
        HasActiveChargingSession::class.java,
    )

    private val getChargingSession: GetChargingSession by KoinJavaComponent.inject(
        GetChargingSession::class.java
    )
    private val observeState: ObserveChargingState by KoinJavaComponent.inject(
        ObserveChargingState::class.java
    )

    private val stopChargingSession: StopChargingSession by KoinJavaComponent.inject(
        StopChargingSession::class.java
    )

    public val isSummaryReady: Flow<Boolean> = observeState().map { it.isSummaryState }

    public val hasActiveSession: Flow<Boolean> = hasActiveChargingSession()

    public val chargeSession: Flow<ChargeSession?> = getChargingSession()
        .map { activeSession ->
            activeSession?.let {
                ChargeSession(
                    evseId = it.evseId,
                    status = it.status,
                    consumption = it.consumption,
                    duration = it.duration
                )
            }
        }

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
