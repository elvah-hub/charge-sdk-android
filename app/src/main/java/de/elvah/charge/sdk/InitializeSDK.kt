package de.elvah.charge.sdk

import android.content.Context
import de.elvah.charge.Elvah
import de.elvah.charge.platform.config.Config

internal fun Context.initializeSdk() {
    Elvah.initialize(
        context = this,
        config = Config(
            "evpk_prod_SxeWcg76aOt3Z1xpDstOA84kGs827Jd53bKebbhLJJcWhpFceq0WKAOoMFkAhmvcaxaBwd4ZeK3omN3FQvkeybm64jLMqbb9Cp0hXuoAe",
        ),
    )
}
