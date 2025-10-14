package de.elvah.charge.public_api.session.usecase

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.domain.usecase.GetActiveChargingSession
import de.elvah.charge.public_api.session.model.ChargeSession
import org.koin.java.KoinJavaComponent

public class GetActiveChargingSession() {

    private val getActiveChargingSession: GetActiveChargingSession by KoinJavaComponent.inject(
        GetActiveChargingSession::class.java
    )

    public suspend operator fun invoke(): Either<Throwable, ChargeSession> {
        return getActiveChargingSession().map {
            ChargeSession(
                evseId = it.evseId,
                status = it.status.name,
                consumption = it.consumption,
                duration = it.duration
            )
        }
    }
}
