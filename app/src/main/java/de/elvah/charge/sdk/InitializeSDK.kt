package de.elvah.charge.sdk

import android.content.Context
import de.elvah.charge.Elvah
import de.elvah.charge.platform.config.Config

internal fun Context.initializeSdk() {
    Elvah.initialize(
        context = this,
        config = Config(
            apiKey = "evpk_test_Syx9tZW1LGcIA7Js0BADiFg7HVDpUGk2CDYPd6zLKOqrSfEI0GIVzInH7W4WfoATHcPnEW7O3uP0GrsGL0IpeUjBf72BuYwanBJ4EUZTv",
        ),
    )
}
