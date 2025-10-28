package de.elvah.charge.features.adhoc_charging.domain.repository

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.data.repository.SessionExceptions
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SummaryInfo
import de.elvah.charge.features.sites.domain.model.AdditionalCosts

internal interface ChargingRepository {

    suspend fun startChargingSession(): Either<SessionExceptions, Boolean>

    suspend fun stopChargingSession(): Either<SessionExceptions, Boolean>

    suspend fun fetchChargingSession(): Either<Throwable, ChargingSession>

    suspend fun getSummary(): SummaryInfo?

    suspend fun resetSession()

    suspend fun updateChargingToken(token: String)

    suspend fun updateOrganisationDetails(organisationDetails: OrganisationDetails)

    suspend fun getAdditionalCosts(): AdditionalCosts?

    suspend fun storeAdditionalCosts(additionalCosts: AdditionalCosts?)
}
