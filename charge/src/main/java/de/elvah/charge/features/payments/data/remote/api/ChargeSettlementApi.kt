package de.elvah.charge.features.payments.data.remote.api

import de.elvah.charge.features.payments.data.remote.model.request.AuthorizeSessionRequest
import de.elvah.charge.features.payments.data.remote.model.request.CreatePaymentIntentRequest
import de.elvah.charge.features.payments.data.remote.model.response.AuthorizeSessionResponse
import de.elvah.charge.features.payments.data.remote.model.response.CreatePaymentIntentResponse
import de.elvah.charge.features.payments.data.remote.model.response.PaymentSummaryDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


internal interface ChargeSettlementApi {

    @POST("/api/payments/initiate")
    suspend fun createPaymentIntent(@Body request: CreatePaymentIntentRequest): CreatePaymentIntentResponse

    @POST("/api/payments/authorize-session")
    suspend fun authorizeSession(@Body request: AuthorizeSessionRequest): AuthorizeSessionResponse

    @GET("/api/payments/{paymentId}/summary")
    suspend fun getPaymentSummary(@Path("paymentId") paymentId: String): PaymentSummaryDto
}
