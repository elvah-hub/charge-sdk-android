package de.elvah.charge.features.adhoc_charging.di

import de.elvah.charge.features.adhoc_charging.domain.usecase.GetChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.HasActiveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.ObserveChargingState
import de.elvah.charge.features.adhoc_charging.domain.usecase.StartChargingSession
import de.elvah.charge.features.adhoc_charging.domain.usecase.StopChargingSession
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val adHocChargingUseCasesModule = module {
    factoryOf(::GetChargingSession)
    factoryOf(::HasActiveChargingSession)
    factoryOf(::ObserveChargingSession)
    factoryOf(::ObserveChargingState)
    factoryOf(::StartChargingSession)
    factoryOf(::StopChargingSession)
}
