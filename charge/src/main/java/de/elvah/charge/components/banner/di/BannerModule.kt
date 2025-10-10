package de.elvah.charge.components.banner.di

import de.elvah.charge.components.banner.BannerComponentSource
import de.elvah.charge.components.banner.BannerComponentSourceImpl
import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

internal val bannerModule = module {
    factory<BannerComponentSource> { (scope: CoroutineScope, siteSource: SitesSource) ->
        BannerComponentSourceImpl(
            coroutineScope = scope,
            sitesSource = siteSource,
            get(),
        )
    }
}

internal fun injectBannerComponentSource(
    coroutineScope: CoroutineScope,
    sitesSource: SitesSource,
): BannerComponentSource {
    return GlobalContext.get().get<BannerComponentSource>(
        parameters = {
            parametersOf(coroutineScope, sitesSource)
        }
    )
}
