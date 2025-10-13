package de.elvah.charge.platform.ui.extension

import androidx.compose.foundation.clickable
import androidx.compose.ui.Modifier

internal inline fun Modifier.debounceClickable(
    debounceInterval: Long = 400,
    crossinline onClick: () -> Unit,
): Modifier {
    var lastClickTime = 0L
    return clickable {
        val currentTime = System.currentTimeMillis()
        if ((currentTime - lastClickTime) < debounceInterval) return@clickable
        lastClickTime = currentTime
        onClick()
    }
}
