package de.elvah.charge

import android.content.Context
import de.elvah.charge.features.adhoc_charging.data.local.DefaultChargingStore
import de.elvah.charge.features.adhoc_charging.data.repository.DefaultChargingRepository
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
import de.elvah.charge.features.sites.di.sitesRepositoriesModule
import de.elvah.charge.features.sites.di.sitesUseCaseModule
import de.elvah.charge.features.sites.di.sitesViewModelModule
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.network.ApiUrlBuilder
import de.elvah.charge.platform.network.okhttp.di.okHttpModule
import de.elvah.charge.platform.network.retrofit.di.retrofitModule
import de.elvah.charge.platform.simulator.di.provideSimulatorModule
import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

object Elvah {

    private val useCaseModule = module {
        includes(sitesUseCaseModule, adHocChargingUseCasesModule, paymentsUseCaseModule)
    }

    private val viewModelsModule = module {
        includes(sitesViewModelModule, adHocViewModelModule)
    }

    val repositoriesModule = module {
        singleOf(::DefaultChargingRepository) { bind<ChargingRepository>() }
        singleOf(::DefaultPaymentsRepository) { bind<PaymentsRepository>() }
        singleOf(::DefaultChargingStore) { bind<ChargingStore>() }
    }

    val networkModule = module {
        singleOf(::ApiUrlBuilder)
        single {
            provideChargingApi(get(), get())
        }
        single {
            provideIntegrateApi(get(), get())
        }

        single {
            provideChargeSettlementApi(get(), get())
        }
        single { de.elvah.charge.features.sites.di.provideApi(get(), get()) }
    }

    val localModule = module {

    }

    private val emptyModule = module {

    }

    fun initialize(context: Context, config: Config) {
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
                viewModelsModule,
                useCaseModule,
                networkModule,
                localModule,
                repositoriesModule,
                okHttpModule,
                retrofitModule,
                sitesRepositoriesModule,
                simulatorModule
            )
        }

        ChargeConfig.initialize(
            de.elvah.charge.platform.config.Config(
                config.apiKey,
                config.darkTheme,
                config.environment
            )
        )
    }
}

data class Config(
    val apiKey: String,
    val environment: Environment = Environment.Simulator(SimulatorFlow.Default),
    //val environment: Environment = Environment.Int,
    val darkTheme: Boolean? = null,
)


sealed class Environment(val value: String) {
    object Production : Environment("prod")
    object Int : Environment("int")
    class Simulator(val simulatorFlow: SimulatorFlow) :
        Environment("simulator/${simulatorFlow.name}")
}

