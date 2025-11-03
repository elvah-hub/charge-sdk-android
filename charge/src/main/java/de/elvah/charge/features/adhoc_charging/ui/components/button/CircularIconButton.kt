package de.elvah.charge.features.adhoc_charging.ui.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

@Composable
internal fun CircularIconButton(
    modifier: Modifier = Modifier,
    iconResId: Int,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondary.copy(
                    alpha = 0.16f,
                ),
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape,
            )
            .clip(shape = CircleShape)
            .clickable(
                enabled = enabled,
                onClick = onClick,
            )
            .padding(
                all = 10.dp
            ),
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.Center),
            painter = painterResource(id = iconResId),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )
    }
}

@PreviewLightDark
@Composable
private fun CircularIconButtonPreview() {
    ElvahChargeTheme {
        CircularIconButton(
            iconResId = R.drawable.ic_battery,
            onClick = {},
        )
    }
}
