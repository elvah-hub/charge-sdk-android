package de.elvah.charge.features.payments.data.mapper

import de.elvah.charge.features.payments.data.remote.model.OrganisationDetailsDto
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts


internal fun OrganisationDetailsDto.toDomain(): OrganisationDetails = OrganisationDetails(
    privacyUrl = privacyUrl.orEmpty(),
    termsOfConditionUrl = termsOfConditionUrl.orEmpty(),
    companyName = companyName.orEmpty(),
    logoUrl = logoUrl.orEmpty(),
    supportContacts = SupportContacts(
        email = supportContacts.find { it.supportType.lowercase() == "email" }?.value,
        phone = supportContacts.find { it.supportType.lowercase() == "phone" }?.value,
        whatsapp = supportContacts.find { it.supportType.lowercase() == "whatsapp" }?.value,
        agent = supportContacts.find { it.supportType.lowercase() == "agent" }?.value,
    )
)