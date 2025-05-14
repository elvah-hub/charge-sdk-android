package de.elvah.charge.features.adhoc_charging.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.elvah.charge.platform.config.ChargeConfig
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import org.koin.compose.KoinContext

class AdHocChargingActivity : ComponentActivity() {

    companion object {
        const val ARG_DEAL_ID = "dealId"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val dealId = intent.extras?.getString(ARG_DEAL_ID).orEmpty()

        setContent {
            ElvahChargeTheme(darkTheme = shouldUseDarkColors(ChargeConfig.config.darkTheme)) {
                KoinContext {
                    AdHocChargingGraph(dealId) {
                        finish()
                    }
                }
            }
        }
    }
}
