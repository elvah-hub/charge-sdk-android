package de.elvah.charge.features.adhoc_charging.domain.repository

import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.sites.domain.model.AdditionalCosts
import kotlinx.coroutines.flow.Flow

internal interface ChargingStore {

    suspend fun setToken(token: String)

    suspend fun setPaymentId(paymentId: String)

    suspend fun setEvseId(evseId: String)

    suspend fun saveOrganisationDetails(organisationDetails: OrganisationDetails)

    suspend fun getAdditionalCosts(): AdditionalCosts?
    suspend fun storeAdditionalCosts(additionalCosts: AdditionalCosts?)

    fun getChargingPrefs(): Flow<ChargingSessionPrefs>

    suspend fun resetSession()
}
