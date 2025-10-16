package de.elvah.charge.features.sites.domain.usecase

import arrow.core.Either
import arrow.core.raise.either
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.public_api.banner.EvseId


internal class GetSite(private val sitesRepository: SitesRepository) {
    operator fun invoke(params: Params): Either<Throwable, ChargeSite> {
        return either {
            val sites = sitesRepository.getChargeSite(params.evseId.value)

            sites.bind()
        }
    }

    internal class Params(
        val evseId: EvseId
    )
}
