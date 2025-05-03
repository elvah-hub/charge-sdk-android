package de.elvah.charge.features.payments.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.elvah.charge.features.payments.data.remote.model.OrganisationDetailsDto

@JsonClass(generateAdapter = true)
internal data class CreatePaymentIntentResponse(

    @Json(name = "data")
    val data: Data,
)

@JsonClass(generateAdapter = true)
internal data class SupportContactsItem(

    @Json(name = "supportType")
    val supportType: String,

    @Json(name = "value")
    val value: String
)

@JsonClass(generateAdapter = true)
internal data class Data(

    @Json(name = "paymentIntentId")
    val paymentIntentId: String,

    @Json(name = "accountId")
    val accountId: String,

    @Json(name = "paymentId")
    val paymentId: String,

    @Json(name = "organisationDetails")
    val organisationDetails: OrganisationDetailsDto,

    @Json(name = "clientSecret")
    val clientSecret: String,

    @Json(name = "authorisationAmount")
    val authorisationAmount: AuthorisationAmount
)

@JsonClass(generateAdapter = true)
internal data class AuthorisationAmount(

    @Json(name = "currency")
    val currency: String,

    @Json(name = "value")
    val value: Double
)
