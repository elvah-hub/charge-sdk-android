package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.data.DefaultFiltersRepository
import de.elvah.charge.features.sites.data.DefaultSitesRepository
import de.elvah.charge.features.sites.domain.repository.FiltersRepository
import de.elvah.charge.features.sites.domain.repository.SitesRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val sitesRepositoriesModule = module {
    singleOf(::DefaultFiltersRepository) { bind<FiltersRepository>() }
    singleOf(::DefaultSitesRepository) { bind<SitesRepository>() }
}
