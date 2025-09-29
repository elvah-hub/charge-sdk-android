package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart.ChargingPointErrorModal
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts
import de.elvah.charge.platform.ui.components.ButtonPrimary
import de.elvah.charge.platform.ui.components.ButtonTertiary
import de.elvah.charge.platform.ui.components.CPOLogo
import de.elvah.charge.platform.ui.components.CopyMedium
import de.elvah.charge.platform.ui.components.ElvahLogo
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.TitleSmall
import de.elvah.charge.platform.ui.components.TopAppBar
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copyMedium
import de.elvah.charge.platform.ui.theme.titleMediumBold
import de.elvah.charge.platform.ui.theme.titleXLargeBold


@Composable
internal fun ActiveChargingScreen(
    viewModel: ActiveChargingViewModel,
    onSupportClick: () -> Unit,
    onStopClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (state) {
        is ActiveChargingState.Loading -> ActiveCharging_Loading()
        is ActiveChargingState.Error -> ActiveCharging_Error()
        is ActiveChargingState.Active -> ActiveCharging_Success(
            state = state as ActiveChargingState.Active,
            onSupportClick = onSupportClick,
            onStopClick = {
                viewModel.stopCharging()
            },
            onBackClick = {
            },
            onDismissError = {
                viewModel.onDismissError()
            }
        )

        is ActiveChargingState.Waiting -> ActiveCharging_Waiting(
            state = state as ActiveChargingState.Waiting, onSupportClick
        )

        is ActiveChargingState.Stopping -> ActiveCharging_Stopping(
            state = state as ActiveChargingState.Stopping, onSupportClick
        )

        is ActiveChargingState.Stopped -> {
            onStopClick()
        }
    }
}

@Composable
private fun ActiveCharging_Loading() {
    FullScreenLoading()
}

@Composable
private fun ActiveCharging_Error() {
    FullScreenError()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ActiveCharging_Success(
    state: ActiveChargingState.Active,
    onSupportClick: () -> Unit,
    onStopClick: () -> Unit,
    onBackClick: () -> Unit,
    onDismissError: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(stringResource(R.string.charging_session_active_title), onBackClick)
    }) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(it)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.size(20.dp))
            CPOLogo(state.activeChargingSessionUI.cpoLogo)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.size(12.dp))
                CopyMedium(
                    text = stringResource(R.string.charging_label),
                )

                Spacer(modifier = Modifier.size(12.dp))

                KilowattsText(state.activeChargingSessionUI.consumption)

                Spacer(modifier = Modifier.size(40.dp))

                val time = getFormattedDuration(state.activeChargingSessionUI.duration)

                if (time.isNotEmpty()) {
                    ChargingTimeText(time)
                }
            }
            Spacer(Modifier.weight(1f))

            ActiveChargingActions(onStopClick, onSupportClick)

            Spacer(Modifier.size(20.dp))
            ElvahLogo()
            Spacer(Modifier.size(20.dp))

            val errorSheetState = rememberModalBottomSheetState()

            LaunchedEffect(state.activeChargingSessionUI.error) {
                if (state.activeChargingSessionUI.error) {
                    errorSheetState.show()
                } else {
                    errorSheetState.hide()
                }
            }

            if (errorSheetState.isVisible) {
                ModalBottomSheet(
                    onDismissRequest = {
                        onDismissError()
                    }, sheetState = errorSheetState
                ) {
                    ChargingPointErrorModal(
                        onCloseModal = onDismissError
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ActiveCharging_Waiting(
    state: ActiveChargingState.Waiting,
    onSupportClick: () -> Unit = {},
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.size(40.dp))
            CPOLogo(state.organisationDetails.logoUrl)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.size(45.dp))

                CircularProgressWithTick(Modifier.size(86.dp))
                Spacer(Modifier.size(45.dp))

                TitleSmall(
                    text = stringResource(R.string.start_label),
                    fontWeight = FontWeight.W700
                )

                CopyMedium(
                    text = stringResource(R.string.start_hint),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 8.dp)
                )

            }
            Spacer(Modifier.weight(1f))

            WaitingChargingActions(onSupportClick)

            Spacer(Modifier.size(20.dp))
            ElvahLogo()
            Spacer(Modifier.size(20.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ActiveCharging_Stopping(
    state: ActiveChargingState.Stopping,
    onSupportClick: () -> Unit = {},
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.size(40.dp))
            CPOLogo(state.organisationDetails.logoUrl)

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(Modifier.size(40.dp))

                Image(painter = painterResource(R.drawable.ic_charging_stop), null)

                Spacer(modifier = Modifier.size(40.dp))
                TitleSmall(
                    stringResource(R.string.stop_charging_session_label),
                    fontWeight = FontWeight.W700,
                    textAlign = TextAlign.Center
                )

                CopyMedium(
                    stringResource(R.string.end_session_message),
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 8.dp),
                    textAlign = TextAlign.Center
                )

            }
            Spacer(Modifier.weight(1f))

            StopChargingActions(onSupportClick)

            Spacer(Modifier.size(20.dp))
            ElvahLogo()
            Spacer(Modifier.size(20.dp))
        }
    }
}

@Composable
private fun CircularProgressWithTick(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(Modifier.fillMaxSize(), color = MaterialTheme.colorSchemeExtended.brand)
        TickIcon(Modifier.fillMaxSize())
    }
}

@Composable
internal fun TickIcon(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.padding(24.dp),
        painter = painterResource(R.drawable.ic_green_tick),
        contentDescription = "",
    )
}

@Composable
private fun KilowattsText(kilowatts: Double, modifier: Modifier = Modifier) {
    val kilowattsText = buildAnnotatedString {
        withStyle(titleXLargeBold.copy(color = MaterialTheme.colorScheme.primary).toSpanStyle()) {
            append("%.3f".format(kilowatts))
        }
        appendLine()

        withStyle(copyMedium.copy(color = MaterialTheme.colorScheme.primary).toSpanStyle()) {
            append(stringResource(R.string.kilowatts_charged_label))
        }
    }
    Text(
        text = kilowattsText,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}


@Composable
private fun ChargingTimeText(time: String, modifier: Modifier = Modifier) {
    val chargingTimeText = buildAnnotatedString {
        withStyle(titleMediumBold.copy(color = MaterialTheme.colorScheme.primary).toSpanStyle()) {
            append(time)
        }
        appendLine()

        withStyle(copyMedium.copy(color = MaterialTheme.colorScheme.primary).toSpanStyle()) {
            append(stringResource(R.string.charging_duration_label))
        }
    }
    Text(
        text = chargingTimeText,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}


internal fun getFormattedDuration(totalSeconds: Int): String {
    val hours = (totalSeconds / 3600).toInt()
    val minutes = ((totalSeconds % 3600) / 60).toInt()
    val seconds = (totalSeconds % 60).toInt()

    return when {
        hours > 0 -> HOURS_TEMPLATE.format(hours, minutes, seconds)
        else -> MINUTES_TEMPLATE.format(minutes, seconds)
    }
}

private const val HOURS_TEMPLATE = "%d hours %d min %02d sec"
private const val MINUTES_TEMPLATE = "%d min %02d sec"


@Composable
private fun ActiveChargingActions(
    onStopClick: () -> Unit,
    onSupportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ButtonPrimary(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.stop_charging_button),
            onClick = onStopClick
        )
        ButtonTertiary(stringResource(R.string.support_button), onClick = onSupportClick)
    }
}

@Composable
private fun WaitingChargingActions(
    onSupportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(45.dp)
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp), color = MaterialTheme.colorSchemeExtended.brand
        )

        ButtonTertiary(stringResource(R.string.support_button), onClick = onSupportClick)
    }
}

@Composable
private fun StopChargingActions(
    onSupportClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp), color = MaterialTheme.colorSchemeExtended.brand
        )
        ButtonTertiary(stringResource(R.string.support_button), onClick = onSupportClick)
    }
}

@Preview
@Composable
private fun ActiveCharging_Success_Preview() {
    ActiveCharging_Success(
        ActiveChargingState.Active(
            activeChargingSessionUI = ActiveChargingSessionUI(
                evseId = "",
                status = "",
                consumption = 0.0,
                duration = 0,
                cpoLogo = "",
                error = false
            )
        ), {}, {}, {}, {})
}

@Preview
@Composable
private fun ActiveCharging_Waiting_Preview() {
    ActiveCharging_Waiting(
        ActiveChargingState.Waiting(
            organisationDetails = OrganisationDetails(
                privacyUrl = "",
                termsOfConditionUrl = "",
                companyName = "",
                logoUrl = "",
                supportContacts = SupportContacts()
            )
        )
    )
}
