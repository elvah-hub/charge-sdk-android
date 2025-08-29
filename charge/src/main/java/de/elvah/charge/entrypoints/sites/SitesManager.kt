package de.elvah.charge.entrypoints.sites

import android.content.Context
import de.elvah.charge.entrypoints.banner.openSite

object SitesManager {
    fun openSite(context: Context, siteId: String) {
        context.openSite(siteId)
    }
}
