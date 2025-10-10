package de.elvah.charge.features.payments.ui.usecase

internal interface InitStripeConfig {
    operator fun invoke(publishableKey: String, accountId: String)
}
