package de.elvah.charge.features.adhoc_charging.ui

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.stripe.android.googlepaylauncher.GooglePayEnvironment
import com.stripe.android.googlepaylauncher.GooglePayLauncher
import de.elvah.charge.features.payments.domain.manager.GooglePayManager
import de.elvah.charge.features.payments.domain.model.GooglePayState
import de.elvah.charge.platform.config.Config
import de.elvah.charge.platform.config.Environment
import de.elvah.charge.platform.di.sdkInject
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.shouldUseDarkColors

internal class AdHocChargingActivity : ComponentActivity() {

    private val config: Config by sdkInject()
    private val googlePayManager: GooglePayManager by sdkInject()

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
                        googlePayManager.setProcessingState()
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
        Log.i("GooglePayLauncher", "Google Pay result: $result")
        when (result) {
            is GooglePayLauncher.Result.Completed -> {
                googlePayManager.processPaymentResult(GooglePayState.Success)
            }

            is GooglePayLauncher.Result.Canceled -> {
                googlePayManager.processPaymentResult(GooglePayState.Cancelled)
            }

            is GooglePayLauncher.Result.Failed -> {
                val errorMessage = result.error.localizedMessage ?: "Payment failed"
                googlePayManager.processPaymentResult(GooglePayState.Failed(errorMessage))
            }
        }
    }
}
