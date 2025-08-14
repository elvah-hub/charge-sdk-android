package de.elvah.charge.features.sites.di

import de.elvah.charge.features.sites.domain.usecase.ClearFilters
import de.elvah.charge.features.sites.domain.usecase.GetBestSite
import de.elvah.charge.features.sites.domain.usecase.GetFilters
import de.elvah.charge.features.sites.domain.usecase.GetSites
import de.elvah.charge.features.sites.domain.usecase.UpdateFilters
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module


val sitesUseCaseModule = module {
    factory { GetFilters(get()) }
    factory { GetBestSite(get(), Dispatchers.Default) }
    factory { GetSites(get()) }
    factory { UpdateFilters(get()) }
    factory { ClearFilters(get()) }
}
