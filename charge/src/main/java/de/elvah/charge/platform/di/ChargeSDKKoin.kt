package de.elvah.charge.platform.di

import android.content.Context
import de.elvah.charge.features.adhoc_charging.di.adHocChargingLocalModule
import de.elvah.charge.features.adhoc_charging.di.adHocChargingUseCasesModule
import de.elvah.charge.features.adhoc_charging.di.adHocViewModelModule
import de.elvah.charge.features.adhoc_charging.di.provideChargingApi
import de.elvah.charge.features.adhoc_charging.data.local.DefaultChargingStore
import de.elvah.charge.features.adhoc_charging.data.repository.DefaultChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingRepository
import de.elvah.charge.features.adhoc_charging.domain.repository.ChargingStore
import de.elvah.charge.features.payments.data.repository.DefaultPaymentsRepository
import de.elvah.charge.features.payments.di.paymentsManagerModule
import de.elvah.charge.features.payments.di.paymentsUseCaseModule
import de.elvah.charge.features.payments.di.provideChargeSettlementApi
import de.elvah.charge.features.payments.di.provideIntegrateApi
import de.elvah.charge.features.payments.domain.repository.PaymentsRepository
import de.elvah.charge.features.sites.di.adaptersModule
import de.elvah.charge.features.sites.di.sitesRepositoriesModule
import de.elvah.charge.features.sites.di.sitesUseCaseModule
import de.elvah.charge.features.sites.di.sitesViewModelModule
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.network.ApiUrlBuilder
import de.elvah.charge.platform.network.okhttp.di.okHttpModule
import de.elvah.charge.platform.network.retrofit.di.retrofitModule
import de.elvah.charge.platform.simulator.di.provideSimulatorModule
import de.elvah.charge.platform.startup.di.startupModule
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.koinApplication
import org.koin.dsl.module

/**
 * Isolated Koin instance for the SDK.
 * This prevents conflicts with apps that also use Koin globally.
 */
internal object ChargeSDKKoin {
    private lateinit var koinApp: KoinApplication
    private var isInitialized = false

    /**
     * Initialize the SDK's isolated Koin instance.
     *
     * @param context Android context
     * @param config SDK configuration
     * @param externalModules Optional list of custom modules from the host app
     */
    fun init(
        context: Context,
        config: Config,
        externalModules: List<Module> = emptyList()
    ) {
        if (isInitialized) {
            koinApp.close()
        }

        koinApp = koinApplication {
            modules(getSDKModules(context, config) + externalModules)
        }
        isInitialized = true
    }

    /**
     * Get the isolated Koin instance.
     * @throws IllegalStateException if not initialized
     */
    fun getKoin(): Koin {
        check(isInitialized) {
            "MySDKKoin is not initialized. Call MySDKKoin.init() first, typically through Elvah.initialize()"
        }
        return koinApp.koin
    }

    /**
     * Close and clean up the Koin instance.
     */
    fun close() {
        if (isInitialized) {
            koinApp.close()
            isInitialized = false
        }
    }

    /**
     * Check if the SDK Koin instance is initialized.
     */
    fun isInitialized(): Boolean = isInitialized

    /**
     * Get all SDK internal modules.
     */
    private fun getSDKModules(context: Context, config: Config): List<Module> {
        val configModule = module {
            single { config }
            single { context }
        }

        val repositoriesModule = module {
            singleOf(::DefaultChargingRepository) { bind<ChargingRepository>() }
            singleOf(::DefaultPaymentsRepository) { bind<PaymentsRepository>() }
            singleOf(::DefaultChargingStore) { bind<ChargingStore>() }
        }

        val useCaseModule = module {
            includes(sitesUseCaseModule, adHocChargingUseCasesModule, paymentsUseCaseModule, paymentsManagerModule)
        }

        val viewModelsModule = module {
            includes(sitesViewModelModule, adHocViewModelModule)
        }

        val networkModule = module {
            includes(adaptersModule)

            singleOf(::ApiUrlBuilder)
            single { provideChargingApi(get(), get()) }
            single { provideIntegrateApi(get(), get()) }
            single { provideChargeSettlementApi(get(), get()) }
            single { de.elvah.charge.features.sites.di.provideApi(get(), get()) }
        }

        val localModule = module {
            includes(adHocChargingLocalModule)
        }

        val simulatorModule = if (config.environment is de.elvah.charge.platform.config.Environment.Simulator) {
            module {
                includes(provideSimulatorModule(config.environment.simulatorFlow))
            }
        } else {
            module { }
        }

        return listOf(
            configModule,
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
}
