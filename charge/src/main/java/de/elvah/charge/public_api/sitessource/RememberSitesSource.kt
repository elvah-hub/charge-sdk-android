package de.elvah.charge.public_api.sitessource

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalInspectionMode
import de.elvah.charge.components.sitessource.SitesSourcePreview
import de.elvah.charge.manager.SitesSourceManager
import de.elvah.charge.manager.di.injectSitesSource
import org.koin.mp.KoinPlatform.getKoin

@Composable
public fun rememberSitesSource(
    instanceId: String? = null,
): SitesSource {
    val inspectionMode = LocalInspectionMode.current

    val manager: SitesSourceManager? = if (inspectionMode) null else getKoin().get()

    val source = rememberSaveable(
        saver = SitesSource.Companion.getSourceSaver(inspectionMode),
        init = {
            if (inspectionMode) {
                SitesSourcePreview()

            } else {
                injectSitesSource(instanceId)
            }
        },
    )

    // called when composable is disposed or the instance changes
    DisposableEffect(source.instanceId) {
        onDispose {
            manager?.scheduleDispose(source.instanceId)
        }
    }

    return source
}
