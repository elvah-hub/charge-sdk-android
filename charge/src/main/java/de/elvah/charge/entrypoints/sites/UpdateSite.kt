package de.elvah.charge.entrypoints.sites

import de.elvah.charge.features.sites.domain.model.ChargeSite
import org.koin.java.KoinJavaComponent
import de.elvah.charge.features.sites.domain.usecase.UpdateSite as UpdateSiteUseCase


class UpdateSite1() {
    private val updateSite: UpdateSiteUseCase by KoinJavaComponent.inject(UpdateSiteUseCase::class.java)

    operator fun invoke(params: Params) {
        updateSite(params = params.toDomain())
    }

    class Params(
        val site: ChargeSite,
    )
}

private fun UpdateSite1.Params.toDomain(): UpdateSiteUseCase.Params =
    UpdateSiteUseCase.Params(site = site)
