package de.elvah.charge.platform.simulator.domain.usecase

import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig

class FakeInitStripeConfig : InitStripeConfig {
    override operator fun invoke(publishableKey: String, accountId: String) {
        // No-op
    }
}
