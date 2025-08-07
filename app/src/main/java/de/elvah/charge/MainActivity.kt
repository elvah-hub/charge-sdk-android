package de.elvah.charge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.elvah.charge.entrypoints.banner.CampaignSource
import de.elvah.charge.entrypoints.banner.ChargeBanner
import de.elvah.charge.entrypoints.banner.EvseId
import de.elvah.charge.features.sites.domain.model.filters.BoundingBox

class MainActivity : ComponentActivity() {
    private var campaignSource: CampaignSource = CampaignSource()

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
                    ChargeBanner()
                }
            }
            LaunchedEffect(Unit) {
              /*  campaignSource.sitesAt(
                    latitude = 14.09499,
                    longitude = -87.19039,
                    radius = 10.0
                )

                campaignSource.sitesAt(
                    BoundingBox(
                        minLat = -87.0,
                        minLng = 14.0,
                        maxLat = -86.0,
                        maxLng = 15.0
                    )
                )

               */

                campaignSource.sitesAt(
                    evseIds = listOf(EvseId("HNTCI*E*00001"))
                )
            }
        }
    }
}
