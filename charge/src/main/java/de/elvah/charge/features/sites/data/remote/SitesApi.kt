package de.elvah.charge.features.sites.data.remote

import de.elvah.charge.features.sites.data.remote.model.request.SignedOfferRequest
import de.elvah.charge.features.sites.data.remote.model.response.OfferDto
import de.elvah.charge.features.sites.data.remote.model.response.ScheduledPricingDto
import de.elvah.charge.features.sites.data.remote.model.response.SignedOfferDto
import de.elvah.charge.features.sites.data.remote.model.response.SitesDto
import de.elvah.charge.features.sites.data.remote.model.response.common.ApiListResponse
import de.elvah.charge.features.sites.data.remote.model.response.common.ApiResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap


internal interface SitesApi {

    @GET("/discovery/sites-offers")
    suspend fun getSites(
        @Query("evseIds") evseIds: List<String>? = null,
        @QueryMap filters: Map<String, String>
    ): ApiListResponse<SitesDto<OfferDto>>

    @POST("/discovery/sites-offers/{siteId}")
    suspend fun getSignedOffer(
        @Path("siteId") siteId: String,
        @Body signedOfferRequest: SignedOfferRequest
    ): ApiResponse<SitesDto<SignedOfferDto>>

    @GET("/discovery/sites/{siteId}/pricing-schedule")
    suspend fun getSiteScheduledPricing(
        @Path("siteId") siteId: String
    ): ApiResponse<ScheduledPricingDto>
}
