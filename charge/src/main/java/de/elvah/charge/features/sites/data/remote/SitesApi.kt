package de.elvah.charge.features.sites.data.remote

import retrofit2.http.GET
import retrofit2.http.Query


internal interface SitesApi {

    @GET("/api/sites-offers")
    suspend fun getSites(
        @Query("minLat") minLat: Double,
        @Query("minLng") minLng: Double,
        @Query("maxLat") maxLat: Double,
        @Query("maxLng") maxLng: Double,
    ): SitesDto
}
