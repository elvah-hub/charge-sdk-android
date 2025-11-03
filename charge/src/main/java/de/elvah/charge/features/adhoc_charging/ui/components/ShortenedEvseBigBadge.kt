package de.elvah.charge.features.adhoc_charging.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.titleLargeBold

@Composable
internal fun ShortenedEvseBigBadge(
    text: String,
    modifier: Modifier = Modifier,
    availability: ChargePointAvailability = ChargePointAvailability.AVAILABLE,
    darkMode: Boolean = isSystemInDarkTheme(),
) {
    val backgroundColor = getBackgroundColor(availability)
    val textColor = getTextColor(availability)
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = Modifier
            .border(
                width = 1.dp,
                shape = shape,
                color = MaterialTheme.colorScheme.secondary.copy(
                    alpha = 0.16f,
                ),
            )
            .background(
                color = backgroundColor,
                shape = shape,
            )
            .clip(shape)
            .height(IntrinsicSize.Max),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize(),
        ) {
            Image(
                painter = painterResource(id = de.elvah.charge.R.drawable.background_half_plug_detail),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .align(Alignment.CenterStart),
                alignment = Alignment.CenterStart,
                contentScale = ContentScale.FillHeight,
                colorFilter = ColorFilter.tint(
                    color = (if (darkMode) Color.White else MaterialTheme.colorSchemeExtended.brand).copy(
                        alpha = 0.7f,
                    ),
                ),
            )
        }

        Text(
            modifier = modifier
                .align(Alignment.Center)
                .padding(
                    horizontal = 19.dp,
                    vertical = 6.dp,
                ),
            text = text,
            style = titleLargeBold,
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.StartEllipsis,
        )
    }
}

@Composable
private fun getTextColor(availability: ChargePointAvailability) = when (availability) {
    ChargePointAvailability.AVAILABLE,
    ChargePointAvailability.UNAVAILABLE,
    ChargePointAvailability.UNKNOWN,
        -> {
        MaterialTheme.colorScheme.primary
    }

    ChargePointAvailability.OUT_OF_SERVICE,
        -> {
        MaterialTheme.colorScheme.primary.copy(
            alpha = TEXT_ALPHA,
        )
    }
}

@Composable
private fun getBackgroundColor(availability: ChargePointAvailability) = when (availability) {
    ChargePointAvailability.AVAILABLE,
        -> {
        MaterialTheme.colorSchemeExtended.brand.copy(
            alpha = BACKGROUND_ALPHA,
        )
    }

    ChargePointAvailability.OUT_OF_SERVICE,
    ChargePointAvailability.UNAVAILABLE,
    ChargePointAvailability.UNKNOWN,
        -> {
        MaterialTheme.colorSchemeExtended.decorativeStroke.copy(
            alpha = BACKGROUND_ALPHA,
        )
    }
}


@PreviewLightDark
@Composable
private fun ShortenedEvseBigBadgePreview() {
    ElvahChargeTheme {
        ShortenedEvseBigBadge(
            text = "1*01",
            availability = ChargePointAvailability.AVAILABLE,
        )
    }
}

@PreviewLightDark
@Composable
private fun OutOfServicePreview() {
    ElvahChargeTheme {
        ShortenedEvseBigBadge(
            text = "1*01",
            availability = ChargePointAvailability.OUT_OF_SERVICE,
        )
    }
}

@PreviewLightDark
@Composable
private fun LongTextPreview() {
    ElvahChargeTheme {
        ShortenedEvseBigBadge(
            text = "1*011*011*011*011*011*011*011*011*011*011*01",
            availability = ChargePointAvailability.OUT_OF_SERVICE,
        )
    }
}

private const val BACKGROUND_ALPHA = 0.1F
private const val TEXT_ALPHA = 0.4F

