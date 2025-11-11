package de.elvah.charge.public_api.extension

import android.content.Context
import de.elvah.charge.features.sites.ui.utils.openSite
import de.elvah.charge.public_api.sitessource.SitesSource

public fun SitesSource.openSite(
    context: Context,
    siteId: String
) {
    context.openSite(siteId, instanceId)
}
