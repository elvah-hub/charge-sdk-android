package de.elvah.charge.features.adhoc_charging.di

import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargeServiceErrors
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargeServiceState
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargeSessionState
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StartChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val adHocChargingUseCasesModule = module {
    factoryOf(::ObserveChargeServiceErrors)
    factoryOf(::ObserveChargeSessionState)
    factoryOf(::ObserveChargingSession)
    factoryOf(::ObserveChargeServiceState)
    factoryOf(::StartChargingSession)
    factoryOf(::StopChargingSession)
}
