package de.elvah.charge.features.adhoc_charging.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.sites.domain.model.BlockingFeeTimeSlot
import de.elvah.charge.features.sites.domain.model.Pricing
import de.elvah.charge.features.sites.extension.formatted
import de.elvah.charge.features.sites.ui.utils.formatTotalOfMinutes
import de.elvah.charge.platform.ui.preview.FontScalePreview
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copyLargeBold
import de.elvah.charge.platform.ui.theme.copyMedium
import de.elvah.charge.platform.ui.theme.copyMediumBold

@Composable
internal fun AdditionalCostsCard(
    activationFee: Pricing?,
    blockingFee: Pricing?,
    blockingFeeMaxPrice: Pricing?,
    startsAfterMinutes: Int?,
    timeSlots: List<BlockingFeeTimeSlot>?,
    modifier: Modifier = Modifier,
) {
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
            .clip(shape)
            .padding(
                horizontal = 12.dp,
                vertical = 16.dp,
            ),
    ) {
        AdditionalCostsContent(
            activationFee = activationFee,
            blockingFee = blockingFee,
            blockingFeeMaxPrice = blockingFeeMaxPrice,
            startsAfterMinutes = startsAfterMinutes,
            timeSlots = timeSlots,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@Composable
internal fun AdditionalCostsContent(
    activationFee: Pricing?,
    blockingFee: Pricing?,
    blockingFeeMaxPrice: Pricing?,
    startsAfterMinutes: Int?,
    timeSlots: List<BlockingFeeTimeSlot>?,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.additional_fee_label),
            style = copyLargeBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(13.dp))
        activationFee?.let {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = stringResource(R.string.activation_fee_label),
                    style = copyMediumBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f),
                )
                Text(
                    text = it.formatted(),
                    style = copyMediumBold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        activationFee?.let {
            blockingFee?.let {
                Spacer(Modifier.height(13.dp))
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorSchemeExtended.decorativeStroke,
                )
                Spacer(Modifier.height(13.dp))
            }
        }

        blockingFee?.let {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                Text(
                    text = stringResource(R.string.blocking_fee_label),
                    style = copyMediumBold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(
                    modifier = Modifier
                        .weight(1f),
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = stringResource(
                            R.string.price_per_minute_label,
                            it.formatted(),
                        ),
                        style = copyMediumBold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    blockingFeeMaxPrice?.let { maxPrice ->
                        Text(
                            text = stringResource(
                                R.string.max_price_label,
                                maxPrice.formatted(),
                            ),
                            style = copyMediumBold,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                startsAfterMinutes?.let { totalMinutes ->
                    val (formatted, _) = formatTotalOfMinutes(
                        context = context,
                        totalMinutes = totalMinutes,
                    )

                    Text(
                        text = stringResource(
                            R.string.blocking_fee_starts_after_label,
                            formatted,
                        ),
                        style = copyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }

                timeSlots?.let { list ->
                    Column {
                        list.forEach { timeSlot ->
                            Text(
                                text = stringResource(R.string.bullet_point_symbol) +
                                        stringResource(
                                            R.string.range_between_two_args_label,
                                            timeSlot.startTime,
                                            timeSlot.endTime,
                                        ),
                                style = copyMedium,
                                color = MaterialTheme.colorScheme.secondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@FontScalePreview
@Composable
private fun AdditionalCostsCardPreview() {
    ElvahChargeTheme {
        AdditionalCostsCard(
            activationFee = Pricing(value = 0.5, currency = "EUR"),
            blockingFee = Pricing(value = 0.5, currency = "EUR"),
            blockingFeeMaxPrice = Pricing(value = 0.5, currency = "EUR"),
            startsAfterMinutes = 10,
            timeSlots = listOf(
                BlockingFeeTimeSlot("10:00", "11:00"),
            ),
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@PreviewLightDark
@Composable
private fun NoActivationFeePreview() {
    ElvahChargeTheme {
        AdditionalCostsCard(
            activationFee = null,
            blockingFee = Pricing(value = 0.5, currency = "EUR"),
            blockingFeeMaxPrice = Pricing(value = 0.5, currency = "EUR"),
            startsAfterMinutes = null,
            timeSlots = listOf(
                BlockingFeeTimeSlot("10:00", "11:00"),
                BlockingFeeTimeSlot("13:00", "14:00"),
                BlockingFeeTimeSlot("20:00", "22:00"),
            ),
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}

@PreviewLightDark
@Composable
private fun NoBlockingFeePreview() {
    ElvahChargeTheme {
        AdditionalCostsCard(
            activationFee = Pricing(value = 0.5, currency = "EUR"),
            blockingFee = null,
            blockingFeeMaxPrice = null,
            startsAfterMinutes = 10,
            timeSlots = null,
            modifier = Modifier
                .fillMaxWidth(),
        )
    }
}
