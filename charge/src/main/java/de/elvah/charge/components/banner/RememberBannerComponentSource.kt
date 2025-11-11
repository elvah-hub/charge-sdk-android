package de.elvah.charge.components.banner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalInspectionMode
import de.elvah.charge.components.banner.di.injectBannerComponentSource
import de.elvah.charge.public_api.sitessource.SitesSource

@Composable
internal fun rememberBannerSource(
    sitesSource: SitesSource,
): BannerComponentSource {
    val inspectionMode = LocalInspectionMode.current
    val coroutineScope = rememberCoroutineScope()

    return remember {
        if (inspectionMode) {
            BannerComponentSourcePreview()

        } else {
            injectBannerComponentSource(coroutineScope, sitesSource)
        }
    }
}
