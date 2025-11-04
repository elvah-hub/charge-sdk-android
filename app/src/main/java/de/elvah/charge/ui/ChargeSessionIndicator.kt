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

@Composable
internal fun ChargeSessionIndicator() {
    val context = LocalContext.current

    val isSummaryReady by ChargeSessionManager.isSummaryReady.collectAsStateWithLifecycle(false)
    val hasActiveSession by ChargeSessionManager.hasActiveSession.collectAsStateWithLifecycle(false)
    val chargingSession by ChargeSessionManager.chargingSession.collectAsStateWithLifecycle(null)

    Column {
        if (hasActiveSession || isSummaryReady) {
            if (isSummaryReady) {
                Text("Charge stopped. Summary is ready.")
            } else {
                Text("Charge session is in progress...")
            }

            Text("Charge: ${chargingSession?.consumption}")
        }

        if (hasActiveSession || isSummaryReady) {
            FlowRow {
                if (isSummaryReady) {
                    Button(onClick = { ChargeSessionManager.openSessionSummary(context) }) {
                        Text("Open summary")
                    }
                } else {
                    Button(onClick = { ChargeSessionManager.openSession(context) }) {
                        Text("Open charge session")
                    }
                }

                if (!isSummaryReady) {
                    Button(onClick = { ChargeSessionManager.stopSession() }) {
                        Text("Stop charge session")
                    }
                }
            }
        }
    }
}
