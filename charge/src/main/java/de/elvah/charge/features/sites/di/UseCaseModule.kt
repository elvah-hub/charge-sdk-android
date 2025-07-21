package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.domain.usecase.GetFilters
import de.elvah.charge.features.sites.domain.usecase.GetSite
import de.elvah.charge.features.sites.domain.usecase.GetSites
import de.elvah.charge.features.sites.domain.usecase.UpdateFilters
import org.koin.dsl.module


val sitesUseCaseModule = module {
    factory { GetFilters(get()) }
    factory { GetSite(get()) }
    factory { GetSites(get()) }
    factory { UpdateFilters(get()) }
}
