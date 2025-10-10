package de.elvah.charge.public_api.sitessource

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalInspectionMode
import de.elvah.charge.components.sitessource.SitesSourcePreview
import de.elvah.charge.manager.SitesSourceManager
import de.elvah.charge.manager.di.injectSitesSource
import org.koin.mp.KoinPlatform.getKoin
import java.util.UUID

@Composable
@NonRestartableComposable
public fun rememberSitesSource(
    instanceId: String? = null,
): SitesSource {
    val rememberSitesSourceId by rememberSaveable {
        mutableStateOf(UUID.randomUUID().toString())
    }

    val inspectionMode = LocalInspectionMode.current

    val manager: SitesSourceManager? = if (inspectionMode) null else getKoin().get()

    val source = rememberSaveable(
        saver = SitesSource.Companion.getSourceSaver(
            clientId = rememberSitesSourceId,
            inspectionMode = inspectionMode
        ),
        init = {
            if (inspectionMode) {
                SitesSourcePreview()

            } else {
                injectSitesSource(
                    clientId = rememberSitesSourceId,
                    instanceId = instanceId,
                )
            }
        },
    )

    DisposableEffect(source.instanceId) {
        onDispose {
            manager?.scheduleDispose(
                clientId = rememberSitesSourceId,
                instanceId = source.instanceId,
            )
        }
    }

    return source
}
