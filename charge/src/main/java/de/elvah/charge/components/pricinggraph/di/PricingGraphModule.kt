package de.elvah.charge.components.pricinggraph.di

import de.elvah.charge.components.pricinggraph.PricingGraphComponentSource
import de.elvah.charge.components.pricinggraph.PricingGraphComponentSourceImpl
import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.CoroutineScope
import org.koin.core.context.GlobalContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

internal val pricingGraphModule = module {
    factory<PricingGraphComponentSource> { (scope: CoroutineScope, siteSource: SitesSource) ->
        PricingGraphComponentSourceImpl(
            coroutineScope = scope,
            sitesSource = siteSource,
        )
    }
}

internal fun injectPricingGraphComponentSource(
    coroutineScope: CoroutineScope,
    sitesSource: SitesSource,
): PricingGraphComponentSource {
    return GlobalContext.get().get<PricingGraphComponentSource>(
        parameters = {
            parametersOf(coroutineScope, sitesSource)
        }
    )
}
