package de.elvah.charge.features.payments.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.elvah.charge.features.payments.data.remote.model.response.SupportContactsItem

@JsonClass(generateAdapter = true)
internal data class OrganisationDetailsDto(
    @Json(name = "privacyUrl")
    val privacyUrl: String?,

    @Json(name = "supportContacts")
    val supportContacts: List<SupportContactsItem>,

    @Json(name = "termsOfConditionUrl")
    val termsOfConditionUrl: String?,

    @Json(name = "companyName")
    val companyName: String?,

    @Json(name = "logoUrl")
    val logoUrl: String?
)
