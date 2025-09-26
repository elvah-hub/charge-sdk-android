package de.elvah.charge.features.adhoc_charging.di

import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.state.BuildSiteDetailSuccessState
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val adHocChargingLocalModule = module {
    factoryOf(::BuildSiteDetailSuccessState)
}
