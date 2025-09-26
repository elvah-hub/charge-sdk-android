package de.elvah.charge.public_api.sites

import android.content.Context
import de.elvah.charge.public_api.banner.openSite

public object SitesManager {
    public fun openSite(context: Context, siteId: String) {
        context.openSite(siteId)
    }
}
