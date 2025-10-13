package de.elvah.charge

import android.app.Application
import de.elvah.charge.interceptor.DummyInterceptor
import de.elvah.charge.interceptor.getChuckerInterceptor
import de.elvah.charge.platform.network.CustomNetworkInterceptorManager
import de.elvah.charge.platform.network.NetworkRequestStorageManager
import de.elvah.charge.sdk.initializeSdk

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Register your custom interceptor BEFORE initializing the SDK
        val customLoggingInterceptor = getChuckerInterceptor(this)
        CustomNetworkInterceptorManager.registerInterceptor(customLoggingInterceptor)

        // Register your custom storage interceptor BEFORE initializing the SDK
        val dummyInterceptor = DummyInterceptor()
        NetworkRequestStorageManager.registerStorageInterceptor(dummyInterceptor)

        initializeSdk()
    }
}
