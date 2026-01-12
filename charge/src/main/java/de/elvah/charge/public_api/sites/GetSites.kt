package de.elvah.charge.public_api.sites

import arrow.core.getOrElse
import de.elvah.charge.features.sites.domain.model.ChargeSite
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.features.sites.domain.model.filters.OfferType
import de.elvah.charge.features.sites.domain.usecase.UpdateSite
import de.elvah.charge.platform.di.sdkInject
import de.elvah.charge.public_api.banner.EvseId
import de.elvah.charge.features.sites.domain.usecase.GetSites as GetSitesUseCase

public class GetSites() {

    private val getSites: GetSitesUseCase by sdkInject()
    private val updateSite: UpdateSite by sdkInject()

    public suspend operator fun invoke(params: Params): List<ChargeSite> {
        return getSites(params = params.toDomain()).getOrElse { emptyList() }.also {
            it.forEach {
                updateSite(UpdateSite.Params(it))
            }
        }
    }

    public class Params(
        public val boundingBox: BoundingBox? = null,
        public val campaignId: String? = null,
        public val organisationId: String? = null,
        public val offerType: OfferType? = null,
        public val evseIds: List<EvseId>? = null,
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
