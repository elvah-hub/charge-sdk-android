package de.elvah.charge.platform.ui.preview

import androidx.compose.ui.tooling.preview.Preview

private const val bigFont = "big font"

@Preview(
    group = bigFont,
    fontScale = 3.5f,
)
internal annotation class BigFontScalePreview

@Preview(
    group = bigFont,
    fontScale = 2f,
)
internal annotation class MediumFontScalePreview
