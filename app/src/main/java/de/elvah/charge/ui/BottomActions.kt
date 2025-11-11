package de.elvah.charge.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import de.elvah.charge.dev.HttpInspectorButton
import de.elvah.charge.mock.ewegoTestSiteEvseIds
import de.elvah.charge.mock.ewegoTestSiteLocation
import de.elvah.charge.mock.tegucigalpaTestSiteLocation
import de.elvah.charge.mock.vattenfallTestSiteLocation
import de.elvah.charge.public_api.extension.openSite
import de.elvah.charge.public_api.sitessource.SitesSource
import kotlinx.coroutines.launch

@Composable
internal fun BottomActions(
    siteIds: List<String>,
    siteIds2: List<String>,
    sitesSource: SitesSource,
    sitesSource2: SitesSource,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        HttpInspectorButton(context)

        FlowRow {
            Button({
                coroutineScope.launch {
                    sitesSource.sitesAt(
                        latitude = tegucigalpaTestSiteLocation.first,
                        longitude = tegucigalpaTestSiteLocation.second,
                        radius = tegucigalpaTestSiteLocation.third,
                    )
                }
            }) {
                Text("Tegucigalpa Charge Inc. Site")
            }

            Button({
                coroutineScope.launch {
                    sitesSource.sitesAt(
                        latitude = vattenfallTestSiteLocation.first,
                        longitude = vattenfallTestSiteLocation.second,
                        radius = vattenfallTestSiteLocation.third,
                    )
                }
            }) {
                Text("Vattenfall Site (heavy api call)")
            }

            Button({
                coroutineScope.launch {
                    sitesSource.sitesAt(
                        latitude = ewegoTestSiteLocation.first,
                        longitude = ewegoTestSiteLocation.second,
                        radius = ewegoTestSiteLocation.third,
                    )
                }
            }) {
                Text("EWEGo Site")
            }

            Button({
                coroutineScope.launch {
                    sitesSource2.sitesAt(
                        evseIds = ewegoTestSiteEvseIds,
                    )
                }
            }) {
                Text("EWEGO Site (by evseId)")
            }
        }

        LazyRow {
            items(siteIds) { siteId ->
                Button({
                    sitesSource.openSite(context, siteId)
                }) {
                    Text("Source Sites: ${siteId.takeLast(5)}")
                }
            }
        }

        LazyRow {
            items(siteIds2) { siteId ->
                Button({
                    sitesSource2.openSite(context, siteId)
                }) {
                    Text("Source 2 Sites: ${siteId.takeLast(5)}")
                }
            }
        }
    }
}
