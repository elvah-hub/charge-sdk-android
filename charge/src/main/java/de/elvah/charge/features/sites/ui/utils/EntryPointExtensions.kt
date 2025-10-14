package de.elvah.charge.features.sites.ui.utils

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingActivity
import de.elvah.charge.features.adhoc_charging.ui.AdHocChargingScreens.ActiveChargingRoute

internal fun Context.openSite(dealId: String) {
    val intent =
        Intent(this, AdHocChargingActivity::class.java).apply {
            putExtra(AdHocChargingActivity.ARG_SITE_ID, dealId)
        }
    startActivity(intent)
}


internal fun Context.goToChargingSession() {
    val deepLinkIntent = Intent(
        Intent.ACTION_VIEW,
        ActiveChargingRoute.route.toUri(),
        this,
        AdHocChargingActivity::class.java
    )

    val deepLinkPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
        addNextIntentWithParentStack(deepLinkIntent)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    deepLinkPendingIntent?.send()
}
