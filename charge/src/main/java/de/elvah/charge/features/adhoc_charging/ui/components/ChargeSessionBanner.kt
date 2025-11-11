package de.elvah.charge.features.adhoc_charging.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.platform.ui.components.buttons.ChevronIcon
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended

@Composable
internal fun ChargeSessionBanner(
    isSummaryReady: Boolean,
    onClick: () -> Unit,
) {
    val text = if (isSummaryReady) {
        "Your session summary is ready"
    } else {
        stringResource(R.string.campaign_banner__active_session_text)
    }

    val color = if (isSummaryReady) {
        MaterialTheme.colorSchemeExtended.brand
    } else {
        MaterialTheme.colorSchemeExtended.decorativeStroke
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .clickable(
                onClick = onClick,
            )
            .background(color)
            .padding(all = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorSchemeExtended.onBrand,
        )

        ChevronIcon(
            tint = MaterialTheme.colorSchemeExtended.onBrand,
        )
    }
}

@Preview
@Composable
private fun ChargeSessionBannerPreview() {
    ElvahChargeTheme {
        ChargeSessionBanner(
            isSummaryReady = true,
            onClick = {},
        )
    }
}
