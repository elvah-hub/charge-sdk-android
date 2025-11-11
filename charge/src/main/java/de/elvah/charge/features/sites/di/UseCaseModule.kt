package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.data.DefaultSitesRepository
import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import de.elvah.charge.features.sites.domain.usecase.ClearFilters
import de.elvah.charge.features.sites.domain.usecase.FindBestSite
import de.elvah.charge.features.sites.domain.usecase.GetFilters
import de.elvah.charge.features.sites.domain.usecase.GetSite
import de.elvah.charge.features.sites.domain.usecase.GetSiteScheduledPricing
import de.elvah.charge.features.sites.domain.usecase.GetSites
import de.elvah.charge.features.sites.domain.usecase.UpdateFilters
import de.elvah.charge.features.sites.domain.usecase.UpdateSiteAvailability
import org.koin.dsl.module

internal val sitesUseCaseModule = module {
    factory { FindBestSite() }
    factory { (repository: SitesRepository) -> GetSite(repository) }
    factory { (repository: SitesRepository) -> GetSites(repository) }
    factory { (repository: SitesRepository) -> UpdateSiteAvailability(repository) }

    factory { (repository: FiltersRepository) -> GetFilters(repository) }
    factory { (repository: FiltersRepository) -> UpdateFilters(repository) }
    factory { (repository: FiltersRepository) -> ClearFilters(repository) }

    // TODO: needs to get the sites repo from the site source!
    // at the moment works as singleton.
    //factory { (repository: SitesRepository) -> GetSiteScheduledPricing(repository) }
    factory { GetSiteScheduledPricing(DefaultSitesRepository(get())) }
}
