package de.elvah.charge.features.adhoc_charging.data.remote.api

import de.elvah.charge.features.adhoc_charging.data.remote.model.response.ActiveChargeSessionsDto
import retrofit2.http.GET
import retrofit2.http.Header


internal interface ChargingApi {

    @GET("/api/direct-charge/user/session")
    suspend fun getActiveChargeSessions(@Header("Authorization") token: String): ActiveChargeSessionsDto

    @GET("/api/direct-charge/user/session/start")
    suspend fun startChargeSessions(@Header("Authorization") token: String)

    @GET("/api/direct-charge/user/session/stop")
    suspend fun stopChargeSession(@Header("Authorization") token: String)
}
