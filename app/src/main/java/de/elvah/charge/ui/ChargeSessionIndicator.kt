package de.elvah.charge.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.public_api.manager.ChargeSessionManager
import de.elvah.charge.public_api.model.ChargingSessionState

@Composable
internal fun ChargeSessionIndicator() {
    val context = LocalContext.current

    val chargeSessionState by ChargeSessionManager.chargingSessionState.collectAsStateWithLifecycle(
        ChargingSessionState(
            isSessionRunning = false,
            isSessionSummaryReady = false,
            lastSessionData = null,
        )
    )

    if (chargeSessionState.isSessionActive) {
        Column {
            if (chargeSessionState.isSessionSummaryReady) {
                Text("Charge stopped. Summary is ready.")
            } else if (chargeSessionState.isSessionRunning) {
                Text("Charge session is in progress...")
            }

            Text("Consumption: ${chargeSessionState.lastSessionData?.consumption}")

            FlowRow {
                if (chargeSessionState.isSessionSummaryReady) {
                    Button(onClick = { ChargeSessionManager.openSessionSummary(context) }) {
                        Text("Open summary")
                    }

                } else {
                    Button(onClick = { ChargeSessionManager.openSession(context) }) {
                        Text("Open charge session")
                    }

                    Button(onClick = { ChargeSessionManager.stopSession() }) {
                        Text("Stop charge session")
                    }
                }
            }
        }
    }
}
