package de.elvah.charge.features.payments.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class AuthorizeSessionResponse(

    @param:Json(name = "data")
    val data: Data,
) {
    @JsonClass(generateAdapter = true)
    internal data class Data(

        @param:Json(name = "chargeIdentityToken")
        val chargeIdentityToken: String
    )
}

