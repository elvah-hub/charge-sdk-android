package de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.flowWithLifecycle
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import de.elvah.charge.R
import de.elvah.charge.public_api.banner.EvseId
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingpointdetail.model.ChargePointDetail
import de.elvah.charge.features.payments.domain.model.PaymentConfiguration
import de.elvah.charge.platform.ui.components.BasicCard
import de.elvah.charge.platform.ui.components.ButtonPrimary
import de.elvah.charge.platform.ui.components.CPOLogo
import de.elvah.charge.platform.ui.components.CopyLarge
import de.elvah.charge.platform.ui.components.CopySmall
import de.elvah.charge.platform.ui.components.ElvahLogo
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.TitleSmall
import de.elvah.charge.platform.ui.components.TopAppBar
import de.elvah.charge.platform.ui.theme.copyLarge
import de.elvah.charge.platform.ui.theme.copyLargeBold
import de.elvah.charge.platform.ui.theme.copySmall
import kotlinx.coroutines.flow.Flow

@Composable
internal fun ChargingPointDetailScreen(
    chargingPointDetailViewModel: ChargingPointDetailViewModel,
    onBackClick: () -> Unit,
    onPaymentSuccess: (String, String) -> Unit,
) {
    val state by chargingPointDetailViewModel.state.collectAsStateWithLifecycle()

    when (val state = state) {
        is ChargingPointDetailState.Loading -> ChargingPointDetail_Loading()
        is ChargingPointDetailState.Error -> ChargingPointDetail_Error()
        is ChargingPointDetailState.Success -> {
            val paymentSheet = remember {
                PaymentSheet.Builder(resultCallback = {
                    onPaymentSheetResult(it)
                    if (it is PaymentSheetResult.Completed) {
                        onPaymentSuccess(
                            state.render.evseId.value,
                            state.paymentIntentParams.paymentId
                        )
                    }
                })
            }.build()

            ChargingPointDetail_Success(
                state,
                onBackClick = onBackClick,
                onAction = {
                    if (state.mocked) {
                        onPaymentSuccess(
                            state.render.evseId.value,
                            state.paymentIntentParams.paymentId
                        )
                    } else {
                        val configuration = PaymentSheet.Configuration(
                            merchantDisplayName = state.chargePointDetail.cpoName,
                        )

                        val currentClientSecret =
                            state.paymentIntentParams.clientSecret
                        presentPaymentSheet(paymentSheet, configuration, currentClientSecret)
                    }
                }
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
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            ChargingPointDetailTopBar(
                title = state.chargePointDetail.chargingPoint,
                onBackClick = onBackClick
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BrandedChargePoint(
                chargePoint = state.chargePointDetail,
                logoUrl = state.render.logoUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(25.dp)
            )

            ChargingPointDetailTariffInfo(
                modifier = Modifier,
                price = state.render.price
            )

            Spacer(modifier = Modifier.weight(1f))

            ChargingPointDetailActions {
                onAction(ChargingPointDetailEvent.OnPayWithCardClicked)
            }

            TermsAndConditions(
                state.render.cpoName,
                state.render.termsUrl,
                state.render.privacyUrl
            )

            ElvahLogo(modifier.padding(top = 20.dp))
        }
    }
}

@Composable
private fun TermsAndConditions(
    cpoName: String,
    termsUrl: String,
    privacyUrl: String,
    modifier: Modifier = Modifier,
) {
    val termsAndConditionsText = buildAnnotatedString {
        append(cpoName)

        append(" ")

        withLink(
            LinkAnnotation.Url(
                termsUrl,
                TextLinkStyles(
                    copySmall.toSpanStyle().copy(
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            )
        ) {
            append(stringResource(R.string.terms_privacy_template_1))
        }

        append(stringResource(R.string.terms_privacy_template_2))

        withLink(
            LinkAnnotation.Url(
                privacyUrl,
                TextLinkStyles(
                    copySmall.toSpanStyle().copy(
                        textDecoration = TextDecoration.Underline,
                        color = MaterialTheme.colorScheme.secondary
                    )
                )
            )
        ) {
            append(stringResource(R.string.terms_privacy_template_3))
        }
    }

    Text(
        text = termsAndConditionsText,
        style = copySmall,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.secondary,
        modifier = modifier
            .padding(horizontal = 64.dp)
            .padding(top = 14.dp)
    )
}

@Composable
private fun ChargingPointDetailTopBar(
    title: String,
    onBackClick: () -> Unit,
) {
    TopAppBar(title, onBackClick)
}

@Composable
private fun BrandedChargePoint(
    chargePoint: ChargePointDetail,
    logoUrl: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        CPOLogo(logoUrl)
        Spacer(modifier = Modifier.size(24.dp))
        ChargePointName(name = chargePoint.chargingPoint)
        Spacer(modifier = Modifier.size(8.dp))
        ChargePointType(type = "${chargePoint.type} - ${chargePoint.energy} kW")
    }
}

@Composable
private fun ChargePointName(
    name: String,
    modifier: Modifier = Modifier,
) {
    TitleSmall(text = name, modifier = modifier)
}

@Composable
private fun ChargePointType(
    type: String,
    modifier: Modifier = Modifier,
) {
    CopySmall(text = type, modifier = modifier)
}

@Composable
private fun ChargingPointDetailTariffInfo(
    price: Double,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(24.dp)) {
        BasicCard {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CopyLarge(stringResource(R.string.energy_label), fontWeight = FontWeight.W700)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val kwhLabel = stringResource(R.string.kwh_label)
                    val priceText = buildAnnotatedString {
                        withStyle(
                            copyLargeBold.toSpanStyle()
                                .copy(color = MaterialTheme.colorScheme.primary)
                        ) {
                            append(price.toString())
                        }
                        withStyle(
                            copyLarge.toSpanStyle()
                                .copy(color = MaterialTheme.colorScheme.secondary),
                        ) {
                            append(kwhLabel)
                        }
                    }
                    Text(priceText)
                }
            }
        }
    }
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

        ButtonPrimary(
            stringResource(R.string.pay_with_credit_card_button),
            onClick = onPayWithCardClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ChargingPointDetail_Error() {
    FullScreenError()
}

@Composable
private fun ChargingPointDetail_Loading() {
    FullScreenLoading()
}

@PreviewLightDark
@Composable
private fun ChargingPointDetail_Success_Preview() {
    ChargingPointDetail_Success(
        state = ChargingPointDetailState.Success(
            evseId = "",
            chargePointDetail = ChargePointDetail(
                chargingPoint = "Charge Point",
                type = "Type",
                offer = ChargePointDetail.Offer(
                    current = 0.24,
                    old = 0.5
                ),
                cpoName = "cpoName",
                evseId = "evseId",
                energy = "",
                signedOffer = "",
                termsUrl = "",
                privacyUrl = ""
            ),
            paymentIntentParams = PaymentConfiguration(
                publishableKey = "",
                accountId = "",
                clientSecret = "",
                paymentId = ""
            ), logoUrl = "",
            render = ChargePointDetailRender(
                evseId = EvseId(""),
                energyType = "",
                energyValue = 0.0f,
                price = 0.0,
                originalPrice = 0.0,
                logoUrl = "logoUrl",
                cpoName = "cpoName",
                termsUrl = "termsUrl",
                privacyUrl = "privacyUrl"
            )
        ),
        onBackClick = {},
        onAction = {}
    )
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

private fun onPaymentSheetResult(paymentSheetResult: PaymentSheetResult) {
    when (paymentSheetResult) {
        is PaymentSheetResult.Canceled -> {
            Log.d("StripeResult", "Canceled")
        }

        is PaymentSheetResult.Failed -> {
            Log.d("StripeResult", "Error: ${paymentSheetResult.error}")
        }

        is PaymentSheetResult.Completed -> {
            // Display for example, an order confirmation screen
            Log.d("StripeResult", "Completed")
        }
    }
}

/**
 * Remembers in the Composition a flow that only emits data when `lifecycle` is
 * at least in `minActiveState`. That's achieved using the `Flow.flowWithLifecycle` operator.
 *
 * Explanation: If flows with operators in composable functions are not remembered, operators
 * will _always_ be called and applied on every recomposition.
 */
@Composable
internal fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycle: Lifecycle = LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED
): Flow<T> = remember(flow, lifecycle) {
    flow.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = minActiveState
    )
}
