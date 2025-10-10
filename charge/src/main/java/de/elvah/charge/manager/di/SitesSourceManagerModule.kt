package de.elvah.charge.manager.di

import de.elvah.charge.components.sitessource.SitesSourcePreview
import de.elvah.charge.components.sitessource.di.sourceFactory
import de.elvah.charge.manager.SitesSourceManager
import de.elvah.charge.public_api.sitessource.SitesSource
import org.koin.core.context.GlobalContext
import org.koin.dsl.module

internal val sitesSourceManagerModule = module {
    single {
        SitesSourceManager(
            sitesSourceFactory = ::sourceFactory,
        )
    }
}

internal fun injectSitesSource(
    instanceId: String? = null,
): SitesSource {
    val manager = GlobalContext.get().get<SitesSourceManager>()

    return manager.getOrCreate(instanceId)
    // TODO: if instance limit is reached, use preview impl, or what to do?
        ?: SitesSourcePreview()
}
