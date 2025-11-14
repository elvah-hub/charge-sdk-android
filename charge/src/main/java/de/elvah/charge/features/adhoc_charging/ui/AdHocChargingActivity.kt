package de.elvah.charge.features.adhoc_charging.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.koinViewModel

internal class AdHocChargingActivity : ComponentActivity() {

    private val config: Config by inject()

    companion object {
        const val ARG_SITE_ID = "siteId"
    }

    private lateinit var googlePayLauncher: GooglePayLauncher


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val siteId = intent.extras?.getString(ARG_SITE_ID).orEmpty()
        initializeGooglePayLauncher()

        setContent {
            ElvahChargeTheme(
                darkTheme = shouldUseDarkColors(config.darkTheme),
                customLightColorScheme = config.customLightColorScheme,
                customDarkColorScheme = config.customDarkColorScheme
            ) {
                AdHocChargingGraph(
                    siteId = siteId,
                    onFinishClicked = { finish() },
                    onGooglePayClick = { clientSecret ->
                        googlePayLauncher.presentForPaymentIntent(clientSecret)
                    }
                )
            }
        }
    }

    private fun initializeGooglePayLauncher() {
        googlePayLauncher = GooglePayLauncher(
            activity = this,
            config = GooglePayLauncher.Config(
                environment = if (config.environment is Environment.Production) {
                    GooglePayEnvironment.Production
                } else {
                    GooglePayEnvironment.Test
                },
                merchantCountryCode = "DE",
                merchantName = "Elvah"
            ),
            readyCallback = { isReady ->
                Log.i("GooglePayLauncher", "Google Pay is ready: $isReady")
                // Google Pay is ready
            },
            resultCallback = ::onGooglePayResult
        )
    }

    private fun onGooglePayResult(result: GooglePayLauncher.Result) {
        Log.i("GooglePayLauncher", "Google Pay is ready: $result")
        when (result) {
            is GooglePayLauncher.Result.Completed -> { /* OK */
            }

            is GooglePayLauncher.Result.Canceled -> { /* Cancelado */
            }

            is GooglePayLauncher.Result.Failed -> { /* Error */
            }
        }
    }
}
