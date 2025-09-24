package de.elvah.charge

import android.app.Application
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.network.CustomNetworkInterceptorManager
import de.elvah.charge.platform.network.NetworkRequestStorageManager

class AppClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // Register your custom interceptor BEFORE initializing the SDK
        val customLoggingInterceptor = CustomLoggingInterceptor()
        CustomNetworkInterceptorManager.registerInterceptor(customLoggingInterceptor)

        // Register your custom storage interceptor BEFORE initializing the SDK
        val dummyInterceptor = DummyInterceptor()
        NetworkRequestStorageManager.registerStorageInterceptor(dummyInterceptor)

        Elvah.initialize(
            this,
            Config(
                "evpk_test_Syx9tZW1LGcIA7Js0BADiFg7HVDpUGk2CDYPd6zLKOqrSfEI0GIVzInH7W4WfoATHcPnEW7O3uP0GrsGL0IpeUjBf72BuYwanBJ4EUZTv",
            ),
        )
    }
}
