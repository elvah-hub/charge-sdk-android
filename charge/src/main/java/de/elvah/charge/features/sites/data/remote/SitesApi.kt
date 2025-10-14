package de.elvah.charge.features.sites.data.remote

import arrow.core.Either
import de.elvah.charge.features.sites.data.remote.model.request.SignedOfferRequest
import de.elvah.charge.features.sites.data.remote.model.response.ScheduledPricingDto
import de.elvah.charge.features.sites.data.remote.model.response.chargepoint.availability.ChargePointAvailabilityResponse
import de.elvah.charge.features.sites.data.remote.model.response.common.ApiListResponse
import de.elvah.charge.features.sites.data.remote.model.response.common.ApiResponse
import de.elvah.charge.features.sites.data.remote.model.response.site.SitesDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

internal interface SitesApi {

    @GET("/discovery/sites-offers")
    suspend fun getSiteOffers(
        @Query("evseIds") evseIds: List<String>? = null,
        @QueryMap filters: Map<String, String>
    ): ApiListResponse<SitesDto>

    @POST("/discovery/sites-offers/{siteId}")
    suspend fun getSiteOffer(
        @Path("siteId") siteId: String,
        @Body signedOfferRequest: SignedOfferRequest,
    ): ApiResponse<SitesDto>

    @GET("/discovery/sites/{siteId}/pricing-schedule")
    suspend fun getSiteScheduledPricing(
        @Path("siteId") siteId: String
    ): Either<Throwable, ApiResponse<ScheduledPricingDto>>

    @GET("/discovery/sites/{siteId}/chargepoint-availabilities")
    suspend fun getChargePointAvailabilities(
        @Path("siteId") siteId: String,
    ): ApiResponse<ChargePointAvailabilityResponse>
}
