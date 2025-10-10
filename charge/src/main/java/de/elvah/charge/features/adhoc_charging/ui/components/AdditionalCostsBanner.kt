package de.elvah.charge.features.adhoc_charging.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.components.button.UnderlinedButton
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.copyMedium

@Composable
internal fun AdditionalCostsBanner(
    onLearnMoreClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(size = 8.dp)

    Row(
        modifier = modifier
            .border(
                width = 1.dp,
                shape = shape,
                color = MaterialTheme.colorScheme.secondary.copy(
                    alpha = 0.16f,
                ),
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = shape,
            )
            .padding(
                horizontal = 12.dp,
                vertical = 16.dp,
            ),
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_monetization),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null,
        )

        Spacer(Modifier.width(13.dp))

        Column {
            Text(
                text = stringResource(R.string.additional_costs_info_label),
                modifier = Modifier
                    .padding(top = 4.dp),
                style = copyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )

            Spacer(Modifier.height(5.dp))

            UnderlinedButton(
                text = stringResource(R.string.learn_more_label),
                onClick = onLearnMoreClicked,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun AdditionalCostsBannerPreview() {
    ElvahChargeTheme {
        AdditionalCostsBanner(
            onLearnMoreClicked = {},
        )
    }
}
