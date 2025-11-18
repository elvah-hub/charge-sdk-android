package de.elvah.charge.platform.simulator.domain.usecase

import de.elvah.charge.features.payments.domain.model.PublishableKey
import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig

internal class FakeInitStripeConfig : InitStripeConfig {
    override operator fun invoke(publishableKey: PublishableKey, accountId: String?) {
        // No-op
    }
}
