package de.elvah.charge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import de.elvah.charge.entrypoints.banner.CampaignBanner
import de.elvah.charge.entrypoints.banner.CampaignSource

class MainActivity : ComponentActivity() {

    private var campaignSource: CampaignSource = CampaignSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            Surface(
                modifier = Modifier
                    .statusBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    CampaignBanner()
                }
            }
            LaunchedEffect(Unit) {
                campaignSource.dealsAt(
                    CampaignSource.Coordinates(
                        minLat = 14.0,
                        minLng = -87.0,
                        maxLat = 15.0,
                        maxLng = -88.0
                    )
                )
            }
        }
    }
}
