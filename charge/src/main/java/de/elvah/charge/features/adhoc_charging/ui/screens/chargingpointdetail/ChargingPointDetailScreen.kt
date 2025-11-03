package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.components.AdditionalCostsCard
import de.elvah.charge.features.adhoc_charging.ui.components.EnergyPriceBanner
import de.elvah.charge.features.adhoc_charging.ui.components.ShortenedEvseBigBadge
import de.elvah.charge.features.adhoc_charging.ui.components.button.CircularIconButton
import de.elvah.charge.features.adhoc_charging.ui.components.button.UnderlinedButton
import de.elvah.charge.features.adhoc_charging.ui.components.discountExpiresAtMock
import de.elvah.charge.features.adhoc_charging.ui.model.AdditionalCostsUI
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.features.sites.domain.model.BlockingFeeTimeSlot
import de.elvah.charge.features.sites.domain.model.ChargePointAvailability
import de.elvah.charge.features.sites.domain.model.Pricing
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.onDebounceClick
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.copySmall
import de.elvah.charge.platform.ui.theme.copyXLargeBold

@Composable
internal fun ChargingPointDetailScreen(
    chargingPointDetailViewModel: ChargingPointDetailViewModel,
    onBackClick: () -> Unit,
    onPaymentSuccess: (String, String) -> Unit,
) {
    val state by chargingPointDetailViewModel.state.collectAsStateWithLifecycle()

    when (val state = state) {
        is ChargingPointDetailState.Loading -> ChargingPointDetail_Loading()
        is ChargingPointDetailState.Error -> ChargingPointDetail_Error(onRetryClick = {
            chargingPointDetailViewModel.onRetryClicked()
        })

        is ChargingPointDetailState.Success -> {
            val paymentSheet = remember {
                PaymentSheet.Builder(resultCallback = {
                    if (it is PaymentSheetResult.Completed) {
                        onPaymentSuccess(
                            state.shortenedEvseId,
                            state.paymentIntentParams.paymentId
                        )
                    }
                })
            }.build()

            ChargingPointDetail_Success(
                state = state,
                onBackClick = onBackClick,
                onAction = {
                    if (state.mocked) {
                        onPaymentSuccess(
                            state.shortenedEvseId,
                            state.paymentIntentParams.paymentId
                        )
                    } else {
                        val configuration = PaymentSheet.Configuration(
                            merchantDisplayName = state.companyName,
                        )

                        val currentClientSecret = state.paymentIntentParams.clientSecret
                        presentPaymentSheet(paymentSheet, configuration, currentClientSecret)
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChargingPointDetail_Success(
    state: ChargingPointDetailState.Success,
    onBackClick: () -> Unit,
    onAction: (ChargingPointDetailEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ChargingPointDetailTopBar(
                onBackClick = onBackClick,
                onActionClick = null, // necessary later to display support and stop charging options
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp,
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                ChargingPointDetailActions(
                    onPayWithCardClick = { onAction(ChargingPointDetailEvent.OnPayWithCardClicked) },
                )

                TermsAndConditions(
                    companyName = state.companyName,
                    termsOfServiceUrl = state.termsOfServiceUrl,
                    privacyPolicyUrl = state.privacyPolicyUrl,
                )

                state.companyLogoUrl?.let {
                    ChargingPartnerAttribution(
                        logoUrl = it,
                    )
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ShortenedEvseBigBadge(
                text = state.shortenedEvseId,
                availability = state.availability,
            )

            EnergyPriceBanner(
                discountExpiresAt = state.discountExpiresAt,
                priceWithLineThrough = state.priceWithLineThrough,
                priceToHighlight = state.priceToHighlight,
            )

            state.additionalCostsUI?.let {
                AdditionalCostsCard(
                    activationFee = it.activationFee,
                    blockingFee = it.blockingFee,
                    blockingFeeMaxPrice = it.blockingFeeMaxPrice,
                    startsAfterMinutes = it.startsAfterMinutes,
                    timeSlots = it.timeSlots,
                )
            }
        }
    }
}

@Composable
private fun TermsAndConditions(
    companyName: String,
    termsOfServiceUrl: String,
    privacyPolicyUrl: String,
    modifier: Modifier = Modifier,
) {
    val fontStyle = copySmall.copy(
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.secondary,
    )

    val legalText = buildAnnotatedString {
        append(companyName)
        append(" ")

        withLink(
            LinkAnnotation.Url(
                url = termsOfServiceUrl,
                styles = TextLinkStyles(
                    fontStyle.toSpanStyle().copy(
                        textDecoration = if (termsOfServiceUrl.isNotBlank())
                            TextDecoration.Underline else TextDecoration.None,
                    ),
                ),
            ),
        ) {
            append(stringResource(R.string.terms_privacy_template_1))
        }

        append(" ")
        append(stringResource(R.string.terms_privacy_template_2))
        append(" ")

        withLink(
            LinkAnnotation.Url(
                url = privacyPolicyUrl,
                styles = TextLinkStyles(
                    fontStyle.toSpanStyle().copy(
                        textDecoration = if (privacyPolicyUrl.isNotBlank())
                            TextDecoration.Underline else TextDecoration.None,
                    )
                ),
            ),
        ) {
            append(stringResource(R.string.terms_privacy_template_3))
        }

        append(" ")
        append(stringResource(R.string.legal_text_apply_label))
    }

    Text(
        text = legalText,
        modifier = modifier,
        style = fontStyle,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.secondary,
    )
}

@Composable
private fun ChargingPartnerAttribution(
    logoUrl: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            modifier = Modifier
                .size(
                    width = 76.dp,
                    height = 46.dp,
                ),
            model = ImageRequest.Builder(LocalContext.current)
                .data(logoUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.ic_logo_elvah_composed),
            contentDescription = null,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChargingPointDetailTopBar(
    onBackClick: () -> Unit,
    onActionClick: (() -> Unit)?,
) {
    TopAppBar(
        modifier = Modifier
            .padding(12.dp),
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.charge_now_label),
                style = copyXLargeBold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
            )
        },
        navigationIcon = {
            CircularIconButton(
                iconResId = R.drawable.ic_chevron_bottom,
                onClick = onBackClick,
            )
        },
        actions = {
            CircularIconButton(
                modifier = Modifier
                    .alpha(if (onActionClick != null) 1f else 0f),
                iconResId = R.drawable.ic_three_dots_vertical,
                enabled = onActionClick != null,
                onClick = { onActionClick?.invoke() },
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
    )
}

@Composable
private fun ChargingPointDetailActions(
    modifier: Modifier = Modifier,
    onPayWithCardClick: () -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        UnderlinedButton(
            text = stringResource(R.string.pay_with_credit_card_button),
            onClick = onDebounceClick(onClick = onPayWithCardClick),
        )
    }
}

@Composable
private fun ChargingPointDetail_Error(onRetryClick: () -> Unit) {
    FullScreenError(onRetryClick = onRetryClick)
}

@Composable
private fun ChargingPointDetail_Loading() {
    FullScreenLoading()
}

@PreviewLightDark
@Composable
private fun ChargingPointDetail_Success_Preview() {
    ElvahChargeTheme {
        ChargingPointDetail_Success(
            state = ChargingPointDetailState.Success(
                evseId = "DE*1*1A*1*03",
                shortenedEvseId = "1*03",
                availability = ChargePointAvailability.AVAILABLE,
                discountExpiresAt = discountExpiresAtMock(),
                priceWithLineThrough = Pricing(0.52, "EUR"),
                priceToHighlight = Pricing(0.42, "EUR"),
                additionalCostsUI = additionalCostsUIMock,
                companyName = "elvah GmbH",
                termsOfServiceUrl = "",
                privacyPolicyUrl = "",
                companyLogoUrl = "",
                paymentIntentParams = PaymentConfiguration(
                    publishableKey = "",
                    accountId = "",
                    clientSecret = "",
                    paymentId = ""
                ),
            ),
            onBackClick = {},
            onAction = {},
        )
    }
}

private fun presentPaymentSheet(
    paymentSheet: PaymentSheet,
    configuration: PaymentSheet.Configuration,
    paymentIntentClientSecret: String,
) {
    paymentSheet.presentWithPaymentIntent(
        paymentIntentClientSecret,
        configuration
    )
}

internal val additionalCostsUIMock = "EUR".let { currency ->
    AdditionalCostsUI(
        activationFee = Pricing(0.5, currency),
        blockingFee = Pricing(0.5, currency),
        blockingFeeMaxPrice = Pricing(0.1, currency),
        startsAfterMinutes = 10,
        timeSlots = listOf(
            BlockingFeeTimeSlot("10:00", "11:00"),
            BlockingFeeTimeSlot("12:00", "13:00"),
            BlockingFeeTimeSlot("15:00", "16:00"),
        ),
    )
}
