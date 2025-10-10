package de.elvah.charge.platform.ui.preview

import androidx.compose.ui.tooling.preview.Preview

private const val bigFont = "big font"


@Preview(
    group = bigFont,
    name = "medium font",
    fontScale = 2f,
)
@Preview(
    group = bigFont,
    name = "big font",
    fontScale = 3.5f,
)
internal annotation class FontScalePreview
