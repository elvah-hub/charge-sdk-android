package de.elvah.charge.platform.ui.extension

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

internal fun PaddingValues.horizontalElement(
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
) = PaddingValues(
    top = 0.dp,
    bottom = 0.dp,
    start = calculateStartPadding(layoutDirection),
    end = calculateEndPadding(layoutDirection),
)

internal val PaddingValues.verticalElement: PaddingValues
    get() = PaddingValues(
        top = calculateTopPadding(),
        bottom = calculateBottomPadding(),
        start = 0.dp,
        end = 0.dp,
    )
