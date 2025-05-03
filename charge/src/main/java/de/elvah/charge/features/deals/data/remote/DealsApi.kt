package de.elvah.charge.features.deals.data.remote

import de.elvah.charge.features.deals.data.remote.model.DealsDto
import de.elvah.charge.features.deals.data.remote.model.SitesDto
import retrofit2.http.GET
import retrofit2.http.Query

internal interface DealsApi {

    @GET("/v1/organisation/sites")
    suspend fun getSites(): SitesDto

    @GET("/api/deals")
    suspend fun getDeals(
        @Query("minLat") minLat: Double,
        @Query("minLng") minLng: Double,
        @Query("maxLat") maxLat: Double,
        @Query("maxLng") maxLng: Double,
    ): DealsDto
}