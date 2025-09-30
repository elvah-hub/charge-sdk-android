package de.elvah.charge.platform.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay

// Lifecycle aware job that performs an action every intervalMillis
@Composable
internal fun Timer(
    intervalMillis: Long = 60_000,
    onComplete: () -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var isActive by remember { mutableStateOf(true) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            isActive = when (event) {
                Lifecycle.Event.ON_START,
                Lifecycle.Event.ON_RESUME,
                    -> true

                Lifecycle.Event.ON_STOP,
                Lifecycle.Event.ON_PAUSE,
                    -> false

                else -> isActive
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isActive) {
        while (isActive) {
            delay(intervalMillis)
            if (isActive) {
                onComplete()
            }
        }
    }
}
