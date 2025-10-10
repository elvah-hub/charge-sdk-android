package de.elvah.charge.components.sitessource.di

import de.elvah.charge.components.sitessource.SitesSourceImpl
import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.features.sites.domain.usecase.ClearFilters
import de.elvah.charge.features.sites.domain.usecase.GetFilters
import de.elvah.charge.features.sites.domain.usecase.GetSite
import de.elvah.charge.features.sites.domain.usecase.GetSiteScheduledPricing
import de.elvah.charge.features.sites.domain.usecase.GetSites
import de.elvah.charge.features.sites.domain.usecase.UpdateFilters
import de.elvah.charge.features.sites.domain.usecase.UpdateSiteAvailability
import de.elvah.charge.public_api.sitessource.SitesSource
import org.koin.core.parameter.parametersOf
import org.koin.core.scope.Scope
import org.koin.dsl.module

internal val sitesSourceModule = module {
    factory<SitesSource> {
        val sitesRepository = get<SitesRepository>()
        val filterRepository = get<FiltersRepository>()

        SitesSourceImpl(
            get(),
            get<GetFilters> { parametersOf(filterRepository) },
            get<UpdateFilters> { parametersOf(filterRepository) },
            get<ClearFilters> { parametersOf(filterRepository) },
            get<GetSite> { parametersOf(sitesRepository) },
            get<GetSites> { parametersOf(sitesRepository) },
            get<GetSiteScheduledPricing> { parametersOf(sitesRepository) },
            get<UpdateSiteAvailability> { parametersOf(sitesRepository) },
        )
    }
}

internal fun Scope.sourceFactory(): SitesSource = get<SitesSource>()
