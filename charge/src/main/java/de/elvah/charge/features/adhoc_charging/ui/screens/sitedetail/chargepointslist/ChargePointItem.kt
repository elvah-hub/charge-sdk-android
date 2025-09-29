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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Price
import de.elvah.charge.features.sites.extension.formatKW
import de.elvah.charge.features.sites.extension.formatted
import de.elvah.charge.features.sites.ui.model.ChargePointUI
import de.elvah.charge.platform.ui.components.CopyMedium
import de.elvah.charge.platform.ui.components.CopySmall
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copyLargeBold
import de.elvah.charge.platform.ui.theme.copyXLargeBold

@Composable
internal fun ChargePointItem(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    chargePoint: ChargePointUI,
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

            Text(
                text = stringResource(statusTextResId),
                style = copyLargeBold,
                color = getAvailabilityColor(
                    availability = chargePoint.availability,
                ),
            )
        }

        Spacer(modifier = Modifier.weight(1.5f))

        Column {
            CopyMedium(
                text = chargePoint.pricePerKwh.formatted(),
                fontWeight = FontWeight.W700
            )

            CopySmall(
                text = chargePoint.maxPowerInKW?.formatKW().orEmpty(),
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
            MaterialTheme.colorSchemeExtended.brand
        }

        ChargePointAvailability.OUT_OF_SERVICE,
        ChargePointAvailability.UNAVAILABLE,
        ChargePointAvailability.UNKNOWN,
            -> {
            MaterialTheme.colorSchemeExtended.decorativeStroke
        }
    }

    Text(
        modifier = Modifier
            .background(
                color = backgroundColor.copy(
                    alpha = 0.2f,
                ),
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
        color = MaterialTheme.colorScheme.primary,
    )
}

@PreviewLightDark
@Composable
private fun ChargePointItemPreview() {
    ElvahChargeTheme {
        ChargePointItem(
            chargePoint = chargePointUIMock,
        )
    }
}

internal val chargePointUIMock = ChargePointUI(
    shortenedEvseId = "1*01",
    maxPowerInKW = 0.42f,
    availability = ChargePointAvailability.AVAILABLE,
    pricePerKwh = Price(22.0, "EUR"),
)
