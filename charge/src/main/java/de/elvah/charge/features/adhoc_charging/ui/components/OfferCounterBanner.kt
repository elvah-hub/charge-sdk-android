package de.elvah.charge.features.adhoc_charging.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.sites.ui.utils.formatTimeUntil
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copySmallBold
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun OfferCounterBanner(
    discountExpiresAt: LocalDateTime,
    modifier: Modifier = Modifier,
    onOfferExpired: () -> Unit,
) {
    val context = LocalContext.current

    formatTimeUntil(context, discountExpiresAt)?.let { (initialFormattedTime, initialDuration) ->
        var formattedTime by remember { mutableStateOf(initialFormattedTime) }
        var duration by remember { mutableStateOf(initialDuration) }

        LaunchedEffect(duration) {
            duration?.let {
                while (true) {
                    delay(it.inWholeSeconds)

                    val result = formatTimeUntil(context, discountExpiresAt)

                    if (result != null) {
                        result.let { (newFormattedTime, newDuration) ->
                            formattedTime = newFormattedTime
                            duration = newDuration
                        }
                    } else {
                        duration = null
                        onOfferExpired()
                    }
                }
            }
        }

        Box(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorSchemeExtended.brand.copy(
                        alpha = 0.1f,
                    ),
                )
                .padding(
                    vertical = 8.dp,
                )
        ) {
            Text(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(
                        horizontal = 16.dp,
                    ),
                text = stringResource(R.string.offer_ends_in) + " " + formattedTime,
                style = copySmallBold,
                color = MaterialTheme.colorSchemeExtended.brand,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalTime::class)
@PreviewLightDark
@Composable
private fun OfferCounterBannerPreview() {
    ElvahChargeTheme {
        OfferCounterBanner(
            discountExpiresAt = Clock.System.now()
                .toLocalDateTime(TimeZone.currentSystemDefault())
                .let {
                    LocalDateTime(
                        year = it.year,
                        month = it.month,
                        day = it.day + 3,
                        hour = it.hour,
                        minute = it.minute,
                        second = it.second,
                    )
                },
            onOfferExpired = {},
        )
    }
}
