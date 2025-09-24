package de.elvah.charge.platform.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val titleXLarge = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 60.sp,
    lineHeight = 70.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)
val titleXLargeBold = titleXLarge.copy(
    fontWeight = FontWeight.W700,
)

val titleLarge = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 36.sp,
    lineHeight = 44.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)

val titleMedium = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)

val titleMediumBold = titleMedium.copy(
    fontWeight = FontWeight.W700,
)

val titleSmall = TextStyle(
    fontWeight = FontWeight.W700,
    fontSize = 20.sp,
    lineHeight = 28.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)

val titleSmallBold = titleSmall.copy(
    fontWeight = FontWeight.W700,
)

val copyXLarge = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 18.sp,
    lineHeight = 26.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)

val copyXLargeBold = copyXLarge.copy(
    fontWeight = FontWeight.W700,
)

val copyLarge = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = (-0.03).sp,
    color = secondary
)

val copyLargeBold = copyLarge.copy(
    fontWeight = FontWeight.W700,
)

val copyMedium = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = (-0.03).sp,
    color = primary
)
val copyMediumBold = copyMedium.copy(
    fontWeight = FontWeight.W700,
)

val copyMediumBoldSmall = copyMedium.copy(
    fontWeight = FontWeight.W700,
    fontSize = 12.sp,
)

val copySmall = TextStyle(
    fontWeight = FontWeight.W400,
    fontSize = 14.sp,
    lineHeight = 20.sp,
    letterSpacing = (-0.03).sp,
    color = secondary
)

val copySmallBold = copySmall.copy(
    fontWeight = FontWeight.W700,
)
