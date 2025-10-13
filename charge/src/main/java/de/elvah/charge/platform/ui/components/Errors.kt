package de.elvah.charge.platform.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import arrow.core.left
import de.elvah.charge.R
import de.elvah.charge.platform.ui.components.buttons.ButtonPrimary
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

@Composable
internal fun FullScreenError(
    modifier: Modifier = Modifier,
    onRetryClick: (() -> Unit)? = null
) {
    Surface {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.error_label),
                Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )

            onRetryClick?.let {
                ButtonPrimary(
                    text = stringResource(R.string.retry),
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    onClick = it
                )
            }
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

@PreviewLightDark
@Composable
private fun FullScreenError_Retry_Preview() {
    ElvahChargeTheme {
        FullScreenError(onRetryClick = {})
    }
}



