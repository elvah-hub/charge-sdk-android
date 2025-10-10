package de.elvah.charge.features.adhoc_charging.domain.usecase

import de.elvah.charge.features.adhoc_charging.data.service.ChargeService

internal class GetActiveChargingSession(
    chargeService: ChargeService,
) {

    val activeSession = chargeService.activeSession
}
