package de.elvah.charge.platform.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import de.elvah.charge.R
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

@Composable
fun CPOLogo(
    url: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    AsyncImage(model = url, contentDescription = contentDescription, modifier = modifier)
}

@Composable
fun ElvahLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        val darkMode = isSystemInDarkTheme()

        CopySmall(stringResource(R.string.powered_by_label))
        Image(
            painter = painterResource(if (darkMode) R.drawable.ic_logo_elvah_composed_dark else R.drawable.ic_logo_elvah_composed),
            contentDescription = null
        )
    }
}

@PreviewLightDark
@Composable
private fun ElvahLogo_Preview() {
    ElvahChargeTheme {
        ElvahLogo()
    }
}