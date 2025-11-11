package de.elvah.charge.util

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable

@Composable
internal fun cutoutCustomInsets(): WindowInsets =
    WindowInsets.safeDrawing.union(WindowInsets.systemBars)
