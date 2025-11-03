package de.elvah.charge

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.public_api.DisplayBehavior
import de.elvah.charge.public_api.banner.ChargeBanner
import de.elvah.charge.public_api.pricinggraph.PricingGraph
import de.elvah.charge.public_api.sitessource.ChargeSitesSource
import de.elvah.charge.public_api.sitessource.rememberSitesSource
import de.elvah.charge.ui.BottomActions
import de.elvah.charge.ui.ChargeSessionIndicator
import de.elvah.charge.util.cutoutCustomInsets

// here you can create a source instance from any context
private var source = ChargeSitesSource.create()

@Composable
internal fun MainScreen() {
    val siteIds by source.siteIds.collectAsStateWithLifecycle()

    // here you can create a source instance in composable context
    val source2 = rememberSitesSource()
    val siteIds2 by source2.siteIds.collectAsStateWithLifecycle()

    Scaffold(
        contentWindowInsets = cutoutCustomInsets(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        bottomBar = {
            BottomActions(
                siteIds = siteIds,
                siteIds2 = siteIds2,
                sitesSource = source,
                sitesSource2 = source2,
                modifier = Modifier
                    .padding(7.dp)
                    .windowInsetsPadding(
                        insets = cutoutCustomInsets()
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
            item {
                ChargeSessionIndicator()
            }

            item {
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

@Preview
@Composable
private fun MainScreenPreview() {
    MainScreen()
}
