package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.domain.model.SummaryInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow


internal class GetSummaryInfo(private val chargingStore: ChargingStore) {

    operator fun invoke(
    ): Flow<SummaryInfo> =
        flow {
            emit(
                chargingStore.getChargingPrefs().first().let {
                    SummaryInfo(it.paymentId, it.logoUrl)
                }
            )
        }
}