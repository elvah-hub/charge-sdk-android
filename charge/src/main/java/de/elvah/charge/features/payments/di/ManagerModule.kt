package de.elvah.charge.features.payments.di

import de.elvah.charge.features.payments.domain.manager.GooglePayManager
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val paymentsManagerModule = module {
    singleOf(::GooglePayManager)
}