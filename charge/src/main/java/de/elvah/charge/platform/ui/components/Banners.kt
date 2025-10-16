package de.elvah.charge.platform.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import kotlinx.coroutines.delay


@Composable
internal fun TickBanner(
    text: String,
    modifier: Modifier = Modifier,
    autoClose: Boolean = true,
    onCloseClick: () -> Unit,
) {
    LaunchedEffect(text, autoClose) {
        if (autoClose) {
            delay(3000)
            onCloseClick()
        }
    }

    Banner(
        text = text,
        icon = R.drawable.ic_tick,
        modifier = modifier,
        onCloseClick = onCloseClick
    )
}

@Composable
internal fun ErrorBanner(
    text: String,
    modifier: Modifier = Modifier,
    autoClose: Boolean = false,
    onCloseClick: () -> Unit,
) {
    LaunchedEffect(text, autoClose) {
        if (autoClose) {
            delay(3000)
            onCloseClick()
        }
    }

    Banner(
        text = text,
        icon = R.drawable.ic_error,
        modifier = modifier,
        onCloseClick = onCloseClick,
        tint = MaterialTheme.colorSchemeExtended.onError,
        containerColor = MaterialTheme.colorSchemeExtended.error
    )
}

@Composable
internal fun Banner(
    text: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    onCloseClick: (() -> Unit)? = null,
    tint: Color = MaterialTheme.colorSchemeExtended.onSuccess,
    containerColor: Color = MaterialTheme.colorSchemeExtended.success
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                tint = tint,
            )

            Spacer(modifier = Modifier.size(12.dp))

            Text(
                text,
                color = tint,
                fontWeight = FontWeight.W600
            )

            onCloseClick?.let {
                Spacer(modifier = Modifier.weight(1f))

                IconButton(onCloseClick) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(R.string.close),
                        tint = tint
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun Banner_Preview() {
    ElvahChargeTheme {
        Banner(text = "Hello", icon = R.drawable.ic_green_tick, onCloseClick = {})
    }
}

@PreviewLightDark
@Composable
private fun ErrorBanner_Preview(){
    ElvahChargeTheme {
        ErrorBanner(text = "Error", onCloseClick = {})
    }
}
