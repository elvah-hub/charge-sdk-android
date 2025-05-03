package de.elvah.charge.features.payments.domain.model

internal class OrganisationDetails(
    val privacyUrl: String,
    val termsOfConditionUrl: String,
    val companyName: String,
    val logoUrl: String,
    val supportContacts: SupportContacts,
)

internal data class SupportContacts(
    val email: String? = null,
    val whatsapp: String? = null,
    val phone: String? = null,
    val agent: String? = null,
)