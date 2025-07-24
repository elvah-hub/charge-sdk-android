package de.elvah.charge

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.elvah.charge.entrypoints.banner.ChargeBanner
import de.elvah.charge.entrypoints.banner.DiscoveryProvider
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox
import de.elvah.charge.platform.simulator.ui.SimulatorActivity

class MainActivity : ComponentActivity() {
    private var discoveryProvider: DiscoveryProvider = DiscoveryProvider()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Surface(
                modifier = Modifier
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Column {
                        ChargeBanner()
                        Button(onClick = {
                            startActivity(Intent(this@MainActivity, SimulatorActivity::class.java))
                        }) {
                            Text("Simulator config")
                        }
                    }
                }
            }
            LaunchedEffect(Unit) {
                discoveryProvider.sitesAt(
                    BoundingBox(
                        minLat = 14.0,
                        minLng = -88.0,
                        maxLat = 15.0,
                        maxLng = -87.0
                    )
                )
            }
        }
    }
}
