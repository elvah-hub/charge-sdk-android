package de.elvah.charge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.dev.HttpInspectorButton
import de.elvah.charge.public_api.DisplayBehavior
import de.elvah.charge.public_api.banner.ChargeBanner
import de.elvah.charge.public_api.extension.openSite
import de.elvah.charge.public_api.model.EvseId
import de.elvah.charge.public_api.pricinggraph.PricingGraph
import de.elvah.charge.public_api.session.SessionManager
import de.elvah.charge.public_api.sitessource.SitesSource
import de.elvah.charge.public_api.sitessource.rememberSitesSource
import kotlinx.coroutines.launch

@Composable
internal fun MainScreen() {
    val context = LocalContext.current
    var hasChargeSessionActive by rememberSaveable { mutableStateOf(false) }

    val source = rememberSitesSource()
    val siteIds by source.siteIds.collectAsStateWithLifecycle()

    val source2 = rememberSitesSource()
    val siteIds2 by source2.siteIds.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        hasChargeSessionActive = SessionManager.isSessionActive()
        hasChargeSessionActive

        val test = 1
        test
    }

    Scaffold(
        contentWindowInsets = getInsets(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        bottomBar = {
            BottomContent(
                siteIds = siteIds,
                siteIds2 = siteIds2,
                sitesSource = source,
                sitesSource2 = source2,
                modifier = Modifier
                    .padding(7.dp)
                    .windowInsetsPadding(
                        insets = getInsets()
                            .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom),
                    ),
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 7.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            if (hasChargeSessionActive) {
                item {
                    Text("Charge session is in progress...")

                    Button(onClick = { SessionManager.openSession(context) }) {
                        Text("Open charge session")
                    }
                }
            }

            item {
                // TODO: do not show banner if no call to sites has been done
                ChargeBanner(
                    sitesSource = source,
                    display = DisplayBehavior.WHEN_SOURCE_SET,
                )
            }

            items(siteIds) { siteId ->
                PricingGraph(
                    sitesSource = source,
                    siteId = siteId,
                    display = DisplayBehavior.WHEN_CONTENT_AVAILABLE,
                )
            }

            item {
                ChargeBanner(
                    sitesSource = source2,
                    display = DisplayBehavior.WHEN_CONTENT_AVAILABLE,
                )
            }

            items(siteIds2) { siteId ->
                PricingGraph(
                    sitesSource = source2,
                    siteId = siteId,
                    display = DisplayBehavior.WHEN_CONTENT_AVAILABLE,
                )
            }
        }
    }
}

@Composable
private fun getInsets(): WindowInsets = WindowInsets.safeDrawing.union(WindowInsets.systemBars)

@Composable
fun BottomContent(
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
                        latitude = 14.103803,
                        longitude = -87.204521,
                        radius = 50.0,
                    )
                }
            }) {
                Text("Tegucigalpa Charge Inc. Site")
            }

            Button({
                coroutineScope.launch {
                    sitesSource.sitesAt(
                        latitude = 52.520008,
                        longitude = 13.404954,
                        radius = 50.0,
                    )
                }
            }) {
                Text("Vattenfall Site (heavy api call)")
            }

            Button({
                coroutineScope.launch {
                    sitesSource.sitesAt(
                        latitude = 53.075833333333,
                        longitude = 8.8072222222222,
                        radius = 10.0
                    )
                }
            }) {
                Text("EWEGo Site")
            }

            Button({
                coroutineScope.launch {
                    sitesSource2.sitesAt(
                        evseIds = listOf(
                            EvseId("DE*2GO*EEWE7418*1A*1"),
                        ),
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

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
