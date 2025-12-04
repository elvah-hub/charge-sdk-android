package de.elvah.charge.platform.startup.di

import de.elvah.charge.platform.startup.SdkLifecycleManager
import de.elvah.charge.platform.startup.StripeInitializationService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val startupModule = module {
    single<CoroutineScope> { CoroutineScope(SupervisorJob() + Dispatchers.IO) }
    singleOf(::StripeInitializationService)
    singleOf(::SdkLifecycleManager)
}