package de.elvah.charge.features.payments.ui.usecase

interface InitStripeConfig {
    operator fun invoke(publishableKey: String, accountId: String)
}
