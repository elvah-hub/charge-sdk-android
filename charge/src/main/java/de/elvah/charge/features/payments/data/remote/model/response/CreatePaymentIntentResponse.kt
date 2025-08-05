package de.elvah.charge.features.payments.data.remote.model.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.elvah.charge.features.payments.data.remote.model.OrganisationDetailsDto

@JsonClass(generateAdapter = true)
internal data class CreatePaymentIntentResponse(

    @param:Json(name = "data")
    val data: Data,
)

@JsonClass(generateAdapter = true)
internal data class SupportContactsItem(

    @param:Json(name = "supportType")
    val supportType: String,

    @param:Json(name = "value")
    val value: String
)

@JsonClass(generateAdapter = true)
internal data class Data(

    @param:Json(name = "paymentIntentId")
    val paymentIntentId: String,

    @param:Json(name = "accountId")
    val accountId: String,

    @param:Json(name = "paymentId")
    val paymentId: String,

    @param:Json(name = "organisationDetails")
    val organisationDetails: OrganisationDetailsDto,

    @param:Json(name = "clientSecret")
    val clientSecret: String,

    @param:Json(name = "authorisationAmount")
    val authorisationAmount: AuthorisationAmount
)

@JsonClass(generateAdapter = true)
internal data class AuthorisationAmount(

    @param:Json(name = "currency")
    val currency: String,

    @param:Json(name = "value")
    val value: Double
)
