package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore


internal class ResetSession(private val chargingStore: ChargingStore) {

    suspend operator fun invoke(){
        chargingStore.resetSession()
    }
}