package de.elvah.charge.features.payments.data.remote.api

import de.elvah.charge.features.payments.data.remote.model.response.GetPublishableKeyResponse
import retrofit2.http.GET


internal interface IntegrateApi {

    @GET("/payments/publishable-key")
    suspend fun getPublishableKey(): GetPublishableKeyResponse
}
