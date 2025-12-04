package de.elvah.charge.features.payments.di

import de.elvah.charge.features.payments.domain.usecase.GetAdditionalCosts
import de.elvah.charge.features.payments.domain.usecase.GetOrganisationDetails
import de.elvah.charge.features.payments.domain.usecase.GetPaymentConfigSettings
import de.elvah.charge.features.payments.domain.usecase.GetPaymentConfiguration
import de.elvah.charge.features.payments.domain.usecase.GetPaymentSummary
import de.elvah.charge.features.payments.domain.usecase.GetPaymentToken
import de.elvah.charge.features.payments.domain.usecase.GetPublishableKey
import de.elvah.charge.features.payments.domain.usecase.GetSessionDetails
import de.elvah.charge.features.payments.domain.usecase.GetSummaryInfo
import de.elvah.charge.features.payments.domain.usecase.ResetSession
import de.elvah.charge.features.payments.domain.usecase.StoreAdditionalCosts
import de.elvah.charge.features.payments.ui.usecase.DefaultInitStripeConfig
import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module


internal val paymentsUseCaseModule = module {
    factoryOf(::GetOrganisationDetails)
    factoryOf(::GetPaymentConfiguration)
    factoryOf(::GetPaymentConfigSettings)
    factoryOf(::GetPaymentSummary)
    factoryOf(::GetPaymentToken)
    factoryOf(::GetPublishableKey)
    factoryOf(::GetSessionDetails)
    factoryOf(::GetSummaryInfo)
    factoryOf(::ResetSession)
    factoryOf(::GetAdditionalCosts)
    factoryOf(::StoreAdditionalCosts)

    factoryOf(::DefaultInitStripeConfig) { bind<InitStripeConfig>() }
}
