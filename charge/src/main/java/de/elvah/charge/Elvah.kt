package de.elvah.charge

import android.content.Context
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.di.ChargeSDKKoin
import de.elvah.charge.platform.di.sdkGet
import de.elvah.charge.platform.startup.SdkLifecycleManager
import org.koin.core.module.Module

public object Elvah {

    private var initialized = false

    /**
     * Initialize the Elvah SDK.
     *
     * @param context Android application context
     * @param config SDK configuration
     * @param customModules Optional list of custom Koin modules for dependency injection.
     *                      Use this to override default SDK implementations.
     */
    public fun initialize(
        context: Context,
        config: Config,
        customModules: List<Module> = emptyList()
    ) {
        if (initialized) {
            return
        }

        // Initialize isolated Koin instance
        ChargeSDKKoin.init(
            context = context.applicationContext,
            config = config,
            externalModules = customModules
        )

        // Initialize lifecycle manager
        val lifecycleManager: SdkLifecycleManager = sdkGet()
        lifecycleManager.initialize()

        initialized = true
    }

    /**
     * Clean up SDK resources.
     * Call this when the SDK is no longer needed.
     */
    public fun cleanup() {
        if (!initialized) {
            return
        }

        // Cleanup lifecycle manager
        val lifecycleManager: SdkLifecycleManager = sdkGet()
        lifecycleManager.cleanup()

        // Close Koin instance
        ChargeSDKKoin.close()

        initialized = false
    }
}
