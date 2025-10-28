package de.elvah.charge.features.sites.ui.utils

import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingActivity
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ActiveChargingRoute

internal fun Context.openSite(dealId: String, sourceInstanceId: String? = null) {
    val intent = Intent(
        this,
        AdHocChargingActivity::class.java
    ).apply {
        putExtra(AdHocChargingActivity.ARG_SOURCE_INSTANCE_ID, sourceInstanceId)
        putExtra(AdHocChargingActivity.ARG_SITE_ID, dealId)
    }

    startActivity(intent)
}

internal fun Context.goToChargingSession(
    isSummaryReady: Boolean,
) {
    val uri = if (isSummaryReady) {
        AdHocChargingScreens.ReviewRoute.route.toUri()
    } else {
        ActiveChargingRoute.route.toUri()
    }

    val intent = Intent(
        Intent.ACTION_VIEW,
        uri,
        this,
        AdHocChargingActivity::class.java,
    )

    startActivity(intent)
}
