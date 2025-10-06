package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.ChargePointItemUI
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.extension.formatKW
import de.elvah.charge.features.sites.extension.formatted
import de.elvah.charge.platform.ui.components.CopyMedium
import de.elvah.charge.platform.ui.components.CopySmall
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copyXLargeBold

@Composable
internal fun ChargePointItem(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    chargePoint: ChargePointItemUI,
) {
    val statusTextResId = getChargePointAvailabilityStatusTextResId(
        availability = chargePoint.availability,
    )

    Row(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            BadgeStatus(
                title = chargePoint.shortenedEvseId,
                availability = chargePoint.availability,
            )

            Spacer(Modifier.height(6.dp))

            CopySmall(
                text = stringResource(statusTextResId),
                color = getAvailabilityColor(
                    availability = chargePoint.availability,
                ),
            )
        }

        Spacer(modifier = Modifier.weight(1.5f))

        Column(
            horizontalAlignment = Alignment.End,
        ) {
            if (chargePoint.hasDiscount) {
                Row {
                    CopyMedium(
                        text = chargePoint.todayPricePerKwh.formatted(),
                        fontWeight = FontWeight.W700
                    )

                    Spacer(Modifier.width(4.dp))

                    CopyMedium(
                        text = chargePoint.standardPricePerKwh.formatted(),
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }
            } else {
                CopyMedium(
                    text = chargePoint.standardPricePerKwh.formatted(),
                    fontWeight = FontWeight.W700
                )
            }

            Spacer(Modifier.height(2.dp))

            CopySmall(
                text = listOfNotNull(
                    chargePoint.powerType,
                    chargePoint.maxPowerInKW?.formatKW(),
                ).joinToString(" • "),
            )
        }

        Spacer(Modifier.width(8.dp))

        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
        )
    }
}

internal fun getChargePointAvailabilityStatusTextResId(
    availability: ChargePointAvailability,
): Int {
    return when (availability) {
        ChargePointAvailability.AVAILABLE,
            -> {
            R.string.charge_point__availability__available
        }

        ChargePointAvailability.OUT_OF_SERVICE,
            -> {
            R.string.charge_point__availability__out_of_service
        }

        ChargePointAvailability.UNAVAILABLE,
        ChargePointAvailability.UNKNOWN,
            -> {
            R.string.charge_point__availability__occupied
        }
    }
}

@Composable
private fun getAvailabilityColor(
    availability: ChargePointAvailability,
): Color {
    return when (availability) {
        ChargePointAvailability.AVAILABLE,
            -> {
            MaterialTheme.colorSchemeExtended.brand
        }

        ChargePointAvailability.OUT_OF_SERVICE,
        ChargePointAvailability.UNAVAILABLE,
        ChargePointAvailability.UNKNOWN,
            -> {
            MaterialTheme.colorScheme.secondary
        }
    }
}

@Composable
private fun BadgeStatus(
    title: String,
    availability: ChargePointAvailability,
) {
    val backgroundColor = when (availability) {
        ChargePointAvailability.AVAILABLE,
            -> {
            MaterialTheme.colorSchemeExtended.brand.copy(
                alpha = 0.2f,
            )
        }

        ChargePointAvailability.OUT_OF_SERVICE,
        ChargePointAvailability.UNAVAILABLE,
        ChargePointAvailability.UNKNOWN,
            -> {
            MaterialTheme.colorSchemeExtended.decorativeStroke.copy(
                alpha = 0.2f,
            )
        }
    }

    val textColor = when (availability) {
        ChargePointAvailability.AVAILABLE,
        ChargePointAvailability.UNAVAILABLE,
        ChargePointAvailability.UNKNOWN,
            -> {
            MaterialTheme.colorScheme.primary
        }

        ChargePointAvailability.OUT_OF_SERVICE,
            -> {
            MaterialTheme.colorScheme.primary.copy(
                alpha = 0.4f,
            )
        }
    }

    Text(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(
                    size = 4.dp,
                ),
            )
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp,
            ),
        text = title,
        style = copyXLargeBold,
        color = textColor,
    )
}

@PreviewLightDark
@Composable
private fun ChargePointItemPreview() {
    ElvahChargeTheme {
        ChargePointItem(
            chargePoint = chargePointItemUIMock,
        )
    }
}

@PreviewLightDark
@Composable
private fun OutOfServicePreview() {
    ElvahChargeTheme {
        ChargePointItem(
            chargePoint = chargePointItemUIMock.copy(
                availability = ChargePointAvailability.OUT_OF_SERVICE,
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun OccupiedPreview() {
    ElvahChargeTheme {
        ChargePointItem(
            chargePoint = chargePointItemUIMock.copy(
                availability = ChargePointAvailability.UNAVAILABLE,
            ),
        )
    }
}

internal val chargePointItemUIMock = ChargePointItemUI(
    evseId = "DE*1*01",
    shortenedEvseId = "1*01",
    availability = ChargePointAvailability.AVAILABLE,
    standardPricePerKwh = Price(22.0, "EUR"),
    maxPowerInKW = 0.42f,
    powerType = "DC",
    todayPricePerKwh = Price(15.0, "EUR"),
    hasDiscount = true,
)
