package de.elvah.charge

import android.app.Application
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.simulator.domain.model.SimulatorFlow

class AppClass : Application() {
    override fun onCreate() {
        super.onCreate()
        Elvah.initialize(
            this,
            Config(
                "evpk_test_Syx9tZW1LGcIA7Js0BADiFg7HVDpUGk2CDYPd6zLKOqrSfEI0GIVzInH7W4WfoATHcPnEW7O3uP0GrsGL0IpeUjBf72BuYwanBJ4EUZTv",
                environment = Environment.Simulator(SimulatorFlow.StartRequestedDelayed)
            ),
        )
    }
}
