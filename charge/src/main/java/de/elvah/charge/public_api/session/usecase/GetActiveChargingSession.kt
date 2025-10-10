package de.elvah.charge.public_api.session.usecase

import de.elvah.charge.features.adhoc_charging.domain.usecase.GetActiveChargingSession
import de.elvah.charge.public_api.session.model.ChargeSession
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import org.koin.java.KoinJavaComponent

public class GetActiveChargingSession() {

    private val getActiveChargingSession: GetActiveChargingSession by KoinJavaComponent.inject(
        GetActiveChargingSession::class.java,
    )

    public suspend operator fun invoke(): ChargeSession? {
        return getActiveChargingSession.activeSession
            .map { session ->
                session?.let {
                    ChargeSession(
                        evseId = it.evseId,
                        status = it.status.name,
                        consumption = it.consumption,
                        duration = it.duration
                    )
                }
            }
            .last()
    }
}
