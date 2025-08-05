package de.elvah.charge.features.payments.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import de.elvah.charge.features.payments.data.remote.model.response.SupportContactsItem

@JsonClass(generateAdapter = true)
internal data class OrganisationDetailsDto(
    @param:Json(name = "privacyUrl")
    val privacyUrl: String?,

    @param:Json(name = "supportContacts")
    val supportContacts: List<SupportContactsItem>,

    @param:Json(name = "termsOfConditionUrl")
    val termsOfConditionUrl: String?,

    @param:Json(name = "companyName")
    val companyName: String?,

    @param:Json(name = "logoUrl")
    val logoUrl: String?
)
