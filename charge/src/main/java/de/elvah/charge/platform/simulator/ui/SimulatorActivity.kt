package de.elvah.charge.platform.simulator.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import org.koin.android.ext.android.inject

internal class SimulatorActivity : ComponentActivity() {

    private val config: Config by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ElvahChargeTheme(
                darkTheme = shouldUseDarkColors(config.darkTheme),
                customLightColorScheme = config.customLightColorScheme,
                customDarkColorScheme = config.customDarkColorScheme
            ) {
                SimulatorGraph {
                    finish()
                }
            }
        }
    }
}