package de.elvah.charge.features.adhoc_charging.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.sites.domain.model.Pricing
import de.elvah.charge.features.sites.extension.formatted
import de.elvah.charge.features.sites.ui.utils.formatTimeUntil
import de.elvah.charge.platform.ui.preview.FontScalePreview
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.copyLarge
import de.elvah.charge.platform.ui.theme.copyLargeBold
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Composable
internal fun EnergyPriceBanner(
    discountExpiresAt: LocalDateTime?,
    priceWithLineThrough: Pricing?,
    priceToHighlight: Pricing,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    val discount = discountExpiresAt
        ?.let { formatTimeUntil(context, it) }

    val shape = RoundedCornerShape(8.dp)

    Column(
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
            .clip(shape),
    ) {
        discount?.let {
            OfferCounterBanner(
                discountExpiresAt = discountExpiresAt,
                onOfferExpired = { /*refresh state if you want to know if a new offer is active */ },
            )
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                )
                .padding(
                    horizontal = 12.dp,
                    vertical = 16.dp,
                ),
            verticalArrangement = Arrangement.Center,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(id = R.string.energy_label),
                style = copyLargeBold,
                color = MaterialTheme.colorScheme.primary,
            )


            FlowRow(
                verticalArrangement = Arrangement.Center,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                priceWithLineThrough?.let {
                    Text(
                        text = it.formatted(),
                        style = copyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.LineThrough,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = priceToHighlight.formatted(),
                        style = copyLargeBold,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Spacer(Modifier.width(2.dp))

                    Text(
                        text = stringResource(R.string.kwh_label),
                        style = copyLarge,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@FontScalePreview
@Composable
private fun EnergyPriceBannerPreview() {
    ElvahChargeTheme {
        EnergyPriceBanner(
            discountExpiresAt = discountExpiresAtMock(),
            priceWithLineThrough = Pricing(50.0, "EUR"),
            priceToHighlight = Pricing(50.0, "EUR"),
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@PreviewLightDark
@FontScalePreview
@Composable
private fun WithoutDiscountPreview() {
    ElvahChargeTheme {
        EnergyPriceBanner(
            discountExpiresAt = null,
            priceWithLineThrough = null,
            priceToHighlight = Pricing(50.0, "EUR"),
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@OptIn(ExperimentalTime::class)
internal fun discountExpiresAtMock(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): LocalDateTime = Clock.System.now()
    .toLocalDateTime(timeZone)
    .toInstant(timeZone)
    .plus(DatePeriod(months = 9, days = 5), timeZone)
    .plus(1, DateTimeUnit.HOUR)
    .toLocalDateTime(timeZone)
