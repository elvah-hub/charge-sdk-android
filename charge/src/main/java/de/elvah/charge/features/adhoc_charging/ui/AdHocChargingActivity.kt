package de.elvah.charge.features.adhoc_charging.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import org.koin.android.ext.android.inject

internal class AdHocChargingActivity : ComponentActivity() {

    private val config: Config by inject()

    companion object {
        const val ARG_SITE_ID = "siteId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val siteId = intent.extras?.getString(ARG_SITE_ID).orEmpty()

        setContent {
            ElvahChargeTheme(darkTheme = shouldUseDarkColors(config.darkTheme)) {
                AdHocChargingGraph(siteId) {
                    finish()
                }
            }
        }
    }
}
