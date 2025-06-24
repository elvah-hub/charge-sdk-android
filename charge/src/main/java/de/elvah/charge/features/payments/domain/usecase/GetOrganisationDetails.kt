package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts


internal class GetOrganisationDetails(private val getSessionDetails: GetSessionDetails) {

    suspend operator fun invoke(
    ): OrganisationDetails? =
        getSessionDetails().let {
            OrganisationDetails(
                it.privacyUrl,
                it.termsOfConditionUrl,
                it.cpoName,
                it.logoUrl,
                SupportContacts(
                    it.email,
                    it.whatsapp,
                    it.phone,
                    it.agent
                )
            )
        }
}