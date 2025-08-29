package de.elvah.charge.entrypoints.sites

import arrow.core.getOrElse
import de.elvah.charge.entrypoints.banner.EvseId
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.usecase.UpdateSite
import org.koin.java.KoinJavaComponent
import de.elvah.charge.features.sites.domain.usecase.GetSites as GetSitesUseCase

class GetSites() {

    private val getSites: GetSitesUseCase by KoinJavaComponent.inject(GetSitesUseCase::class.java)
    private val updateSite: UpdateSite by KoinJavaComponent.inject(UpdateSite::class.java)

    suspend operator fun invoke(params: Params): List<ChargeSite> {
        return getSites(params = params.toDomain()).getOrElse { emptyList() }.also {
            it.forEach {
                updateSite(UpdateSite.Params(it))
            }
        }
    }

    class Params(
        val boundingBox: BoundingBox? = null,
        val campaignId: String? = null,
        val organisationId: String? = null,
        val offerType: OfferType? = null,
        val evseIds: List<EvseId>? = null,
    )
}

private fun GetSites.Params.toDomain(): GetSitesUseCase.Params =
    GetSitesUseCase.Params(
        boundingBox = boundingBox,
        campaignId = campaignId,
        organisationId = organisationId,
        offerType = offerType,
        evseIds = evseIds
    )
