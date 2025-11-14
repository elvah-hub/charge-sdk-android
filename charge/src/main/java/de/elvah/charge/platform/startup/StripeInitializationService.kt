package de.elvah.charge.platform.startup

import android.util.Log
import de.elvah.charge.features.payments.domain.usecase.GetPublishableKey
import de.elvah.charge.features.payments.ui.usecase.InitStripeConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class StripeInitializationService(
    private val getPublishableKey: GetPublishableKey,
    private val initStripeConfig: InitStripeConfig,
    private val coroutineScope: CoroutineScope
) {
    private var initializationJob: Job? = null

    fun initialize() {
        initializationJob?.cancel()
        initializationJob = coroutineScope.launch {
            getPublishableKey().fold(
                ifLeft = { throwable ->
                    Log.w(TAG, "Failed to initialize Stripe configuration", throwable)
                },
                ifRight = { publishableKey ->
                    try {
                        initStripeConfig(publishableKey, null)
                        Log.d(TAG, "Stripe configuration initialized successfully")
                    } catch (e: Exception) {
                        Log.e(TAG, "Failed to configure Stripe", e)
                    }
                }
            )
        }
    }

    fun cleanup() {
        initializationJob?.cancel()
        initializationJob = null
    }

    private companion object {
        private const val TAG = "StripeInitializationService"
    }
}
