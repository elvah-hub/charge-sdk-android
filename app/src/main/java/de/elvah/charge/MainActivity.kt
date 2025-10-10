package de.elvah.charge

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import de.elvah.charge.public_api.banner.ChargeBanner
import de.elvah.charge.public_api.banner.ChargeBannerSource
import de.elvah.charge.public_api.banner.EvseId
import de.elvah.charge.public_api.pricinggraph.PricingGraph
import de.elvah.charge.public_api.sites.GetSites
import de.elvah.charge.public_api.sites.SitesManager

class MainActivity : ComponentActivity() {
    private var chargeBannerSource: ChargeBannerSource = ChargeBannerSource()
    private var getSites: GetSites = GetSites()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val sites by produceState(emptyList()) {
                value = getSites(
                    GetSites.Params(
                        evseIds = listOf(EvseId("DE*2GO*EEWE7418*1A*1"))
                    )
                )
            }
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
                        Button({
                            SitesManager.openSite(context, sites.first().id)
                        }) {
                            Text("Open Deal at HNTCI*E*00001")
                        }
                        
                        // Test PricingGraph with sample site ID
                        if (sites.isNotEmpty()) {
                            PricingGraph(
                                siteId = sites.first().id,
                                minYAxisPrice = 0.0
                            )
                        }
                    }
                }
            }
            LaunchedEffect(Unit) {

                /*  chargeBannerSource.sitesAt(
                      latitude = 53.075833333333,
                      longitude = 8.8072222222222,
                      radius = 10.0
                  )

                 */

                /*
                  campaignSource.sitesAt(
                      BoundingBox(
                          minLat = -87.0,
                          minLng = 14.0,
                          maxLat = -86.0,
                          maxLng = 15.0
                      )
                  )

                 */

                /*
                campaignSource.sitesAt(
                    evseIds = listOf(EvseId("HNTCI*E*00001"))
                )

                 */
            }
        }
    }
}
