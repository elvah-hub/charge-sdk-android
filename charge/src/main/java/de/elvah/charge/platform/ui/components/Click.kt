package de.elvah.charge.platform.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
internal fun onDebounceClick(
    debounceTimeMillis: Long = 500L,
    onClick: () -> Unit,
): () -> Unit {
    var lastClickTimeMillis: Long by remember { mutableLongStateOf(value = 0L) }
    return {
        System.currentTimeMillis().let { currentTimeMillis ->
            if ((currentTimeMillis - lastClickTimeMillis) >= debounceTimeMillis) {
                lastClickTimeMillis = currentTimeMillis
                onClick()
            }
        }
    }
}
