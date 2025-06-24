package de.elvah.charge.features.payments.domain.mapper

import de.elvah.charge.features.adhoc_charging.ChargingSessionPrefs
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts

internal fun ChargingSessionPrefs.getOrganisationDetails(): OrganisationDetails =
    OrganisationDetails(
        privacyUrl = privacyUrl,
        termsOfConditionUrl = termsOfConditionUrl,
        companyName = cpoName,
        logoUrl = logoUrl,
        supportContacts = SupportContacts(
            email = email,
            whatsapp = whatsapp,
            phone = phone,
            agent = agent
        )
    )
