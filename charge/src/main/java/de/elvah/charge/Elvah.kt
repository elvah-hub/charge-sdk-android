package de.elvah.charge

import android.content.Context
import de.elvah.charge.features.adhoc_charging.data.local.DefaultChargingStore
import de.elvah.charge.features.adhoc_charging.data.repository.DefaultChargingRepository
import de.elvah.charge.features.adhoc_charging.di.adHocChargingLocalModule
import de.elvah.charge.features.adhoc_charging.di.adHocChargingUseCasesModule
import de.elvah.charge.features.adhoc_charging.di.adHocViewModelModule
import de.elvah.charge.features.adhoc_charging.di.provideChargingApi
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.data.repository.DefaultPaymentsRepository
import de.elvah.charge.features.payments.di.paymentsUseCaseModule
import de.elvah.charge.features.payments.di.provideChargeSettlementApi
import de.elvah.charge.features.payments.di.provideIntegrateApi
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.features.sites.di.adaptersModule
import de.elvah.charge.features.sites.di.sitesRepositoriesModule
import de.elvah.charge.features.sites.di.sitesUseCaseModule
import de.elvah.charge.features.sites.di.sitesViewModelModule
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.network.ApiUrlBuilder
import de.elvah.charge.platform.network.okhttp.di.okHttpModule
import de.elvah.charge.platform.network.retrofit.di.retrofitModule
import de.elvah.charge.platform.simulator.di.provideSimulatorModule
import de.elvah.charge.platform.startup.SdkLifecycleManager
import de.elvah.charge.platform.startup.di.startupModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.inject

public object Elvah {

    private val useCaseModule = module {
        includes(sitesUseCaseModule, adHocChargingUseCasesModule, paymentsUseCaseModule)
    }

    private val viewModelsModule = module {
        includes(sitesViewModelModule, adHocViewModelModule)
    }

    private val repositoriesModule = module {
        singleOf(::DefaultChargingRepository) { bind<ChargingRepository>() }
        singleOf(::DefaultPaymentsRepository) { bind<PaymentsRepository>() }
        singleOf(::DefaultChargingStore) { bind<ChargingStore>() }
    }

    private fun configModule(config: Config) = module {
        single { config }
    }

    private val networkModule = module {
        includes(adaptersModule)

        singleOf(::ApiUrlBuilder)
        single { provideChargingApi(get(), get()) }
        single { provideIntegrateApi(get(), get()) }
        single { provideChargeSettlementApi(get(), get()) }
        single { de.elvah.charge.features.sites.di.provideApi(get(), get()) }
    }

    private val localModule = module {
        includes(adHocChargingLocalModule)
    }

    private val emptyModule = module {

    }

    public fun initialize(context: Context, config: Config) {
        val simulatorModule = if (config.environment is Environment.Simulator) {
            module {
                includes(provideSimulatorModule(config.environment.simulatorFlow))
            }
        } else {
            emptyModule
        }

        startKoin {
            androidLogger()
            androidContext(context)
            modules(
                configModule(config),
                viewModelsModule,
                useCaseModule,
                networkModule,
                localModule,
                repositoriesModule,
                okHttpModule,
                retrofitModule,
                sitesRepositoriesModule,
                simulatorModule,
                startupModule,
            )
        }
        
        val lifecycleManager: SdkLifecycleManager by inject(SdkLifecycleManager::class.java)
        lifecycleManager.initialize()
    }
    
    public fun cleanup() {
        val lifecycleManager: SdkLifecycleManager by inject(SdkLifecycleManager::class.java)
        lifecycleManager.cleanup()
    }
}
