package de.elvah.charge.public_api.sites

import android.content.Context
import de.elvah.charge.features.sites.ui.utils.openChargePoint
import de.elvah.charge.features.sites.ui.utils.openSite

public object SitesManager {
    public fun openSite(context: Context, siteId: String) {
        context.openSite(siteId)
    }


    public fun openChargePoint(context: Context, evseId: String) {
        context.openChargePoint(evseId)
    }
}
