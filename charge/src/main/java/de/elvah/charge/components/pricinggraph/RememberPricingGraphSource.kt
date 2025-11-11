package de.elvah.charge.components.pricinggraph

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalInspectionMode
import de.elvah.charge.components.pricinggraph.di.injectPricingGraphComponentSource
import de.elvah.charge.public_api.sitessource.SitesSource

@Composable
internal fun rememberPricingGraphSource(
    sitesSource: SitesSource,
): PricingGraphComponentSource {
    val inspectionMode = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    return remember {
        if (inspectionMode) {
            PricingGraphComponentSourcePreview()

        } else {
            injectPricingGraphComponentSource(coroutineScope, sitesSource)
        }
    }
}
