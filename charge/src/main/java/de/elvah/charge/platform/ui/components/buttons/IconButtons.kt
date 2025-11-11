package de.elvah.charge.platform.ui.components.buttons

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import de.elvah.charge.R
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

@Composable
internal fun Chevron(onClick: () -> Unit = {}) {
    IconButton(onClick = onClick) {
        ChevronIcon()
    }
}

@Composable
internal fun ChevronIcon(
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorScheme.primary,
) {
    Icon(
        modifier = modifier,
        painter = painterResource(R.drawable.ic_chevron_right),
        tint = tint,
        contentDescription = null,
    )
}

@PreviewLightDark
@Composable
private fun Chevron_Preview() {
    ElvahChargeTheme { Chevron() }
}
