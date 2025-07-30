package de.elvah.charge.platform.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import de.elvah.charge.R
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

@Composable
fun FullScreenError(modifier: Modifier = Modifier) {
    Surface {
        Box(modifier = modifier.fillMaxSize()) {
            Text(
                stringResource(R.string.error_label),
                Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun FullScreenError_Preview() {
    ElvahChargeTheme {
        FullScreenError()
    }
}
