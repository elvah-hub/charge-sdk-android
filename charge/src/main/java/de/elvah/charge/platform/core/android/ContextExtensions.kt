package de.elvah.charge.platform.core.android

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

internal fun Context.openMap(lat: Double, lng: Double, title: String) {
    val gmmIntentUri: Uri =
        "geo:?q=$lat,$lng($title)".toUri()
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
    startActivity(mapIntent)
}
