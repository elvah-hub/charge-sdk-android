package de.elvah.charge.features.payments.ui.usecase

import de.elvah.charge.features.payments.domain.model.PublishableKey

internal interface InitStripeConfig {
    operator fun invoke(publishableKey: PublishableKey, accountId: String?)
}
