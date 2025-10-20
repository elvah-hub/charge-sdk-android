package de.elvah.charge

import android.app.Application
import de.elvah.charge.sdk.initializeSdk

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initializeSdk()
    }
}
