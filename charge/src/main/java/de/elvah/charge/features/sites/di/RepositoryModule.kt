package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.data.DefaultFiltersRepository
import de.elvah.charge.features.sites.data.DefaultSitesRepository
import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import org.koin.dsl.module

internal val sitesRepositoriesModule = module {
    factory<SitesRepository> { DefaultSitesRepository(get()) }
    factory<FiltersRepository> { DefaultFiltersRepository() }
}
