package de.elvah.charge.features.adhoc_charging.domain.repository

import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import kotlinx.coroutines.flow.Flow

internal interface ChargingStore {

    suspend fun setToken(token: String)

    suspend fun setPaymentId(paymentId: String)
    suspend fun setEvseId(evseId: String)

    suspend fun saveOrganisationDetails(organisationDetails: OrganisationDetails)

    fun getChargingPrefs(): Flow<ChargingSessionPrefs>

    suspend fun resetSession()
}
