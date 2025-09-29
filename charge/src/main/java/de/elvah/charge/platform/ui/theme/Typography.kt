package de.elvah.charge.platform.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


internal val titleXLarge = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 60.sp,
    lineHeight = 70.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)
internal val titleXLargeBold = titleXLarge.copy(
    fontWeight = FontWeight.W700,
)

internal val titleLarge = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)

internal val titleMedium = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)

internal val titleMediumBold = titleMedium.copy(
    fontWeight = FontWeight.W700,
)

internal val titleSmall = TextStyle(
    fontWeight = FontWeight.W700,
    fontSize = 20.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)

internal val titleSmallBold = titleSmall.copy(
    fontWeight = FontWeight.W700,
)

internal val copyXLarge = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 18.sp,
    lineHeight = 26.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)

internal val copyXLargeBold = copyXLarge.copy(
    fontWeight = FontWeight.W700,
)

internal val copyLarge = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = (-0.03).sp,
    color = secondary
)

internal val copyLargeBold = copyLarge.copy(
    fontWeight = FontWeight.W700,
)

internal val copyMedium = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)
internal val copyMediumBold = copyMedium.copy(
    fontWeight = FontWeight.W700,
)

internal val copySmall = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = (-0.03).sp,
    color = secondary
)

internal val copySmallBold = copySmall.copy(
    fontWeight = FontWeight.W700,
)
