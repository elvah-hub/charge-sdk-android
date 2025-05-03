package de.elvah.charge.features.adhoc_charging.domain.repository

import arrow.core.Either
import de.elvah.charge.features.adhoc_charging.domain.model.ChargingSession
import kotlinx.coroutines.flow.Flow

internal interface ChargingRepository {

    val activeSessions: Flow<ChargingSession?>

    fun updateChargingToken(token: String)

    suspend fun fetchChargingSession(): Either<Exception, ChargingSession>

    suspend fun startChargingSession()

    suspend fun stopChargingSession()
}
