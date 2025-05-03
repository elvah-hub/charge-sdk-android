package de.elvah.charge.features.payments.domain.usecase

import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository




internal class GetOrganisationDetails(private val paymentsRepository: PaymentsRepository) {

    operator fun invoke(
    ): OrganisationDetails? =
        paymentsRepository.organisationDetails
}