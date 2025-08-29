package de.elvah.charge.features.sites.domain.usecase

import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.repository.SitesRepository


internal class UpdateSite(private val sitesRepository: SitesRepository) {
    operator fun invoke(params: Params) {
        sitesRepository.updateChargeSite(
            params.site
        )
    }

    internal class Params(
        val site: ChargeSite,
    )
}
