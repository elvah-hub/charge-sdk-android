package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import kotlinx.coroutines.flow.first


internal class GetSessionDetails(private val chargingStore: ChargingStore) {

    suspend operator fun invoke(
    ): ChargingSessionPrefs =
        chargingStore.getChargingPrefs().first()
}