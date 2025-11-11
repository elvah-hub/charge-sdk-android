package de.elvah.charge.public_api.di

import de.elvah.charge.components.banner.di.bannerModule
import de.elvah.charge.components.pricinggraph.di.pricingGraphModule
import de.elvah.charge.components.sitessource.di.sitesSourceModule
import de.elvah.charge.manager.di.sitesSourceManagerModule
import org.koin.dsl.module

internal val componentsModule = module {
    includes(
        sitesSourceManagerModule,
        sitesSourceModule,
        bannerModule,
        pricingGraphModule,
    )
}
