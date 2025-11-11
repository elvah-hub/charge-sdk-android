package de.elvah.charge.features.sites.domain.usecase

import arrow.core.Either
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.repository.SitesRepository

internal class UpdateSiteAvailability(
    private val sitesRepository: SitesRepository,
) {

    suspend operator fun invoke(siteId: String): Either<Throwable, List<ChargeSite.ChargePoint>> {
        return sitesRepository.updateChargePointAvailabilities(siteId)
    }
}
