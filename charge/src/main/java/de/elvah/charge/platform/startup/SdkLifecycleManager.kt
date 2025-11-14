package de.elvah.charge.platform.startup

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

internal class SdkLifecycleManager(
    private val stripeInitializationService: StripeInitializationService,
    private val sdkScope: CoroutineScope
) {

    fun initialize() {
        stripeInitializationService.initialize()
    }

    fun cleanup() {
        stripeInitializationService.cleanup()
        sdkScope.cancel()
    }
}