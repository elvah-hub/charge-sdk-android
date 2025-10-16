package de.elvah.charge.features.adhoc_charging.ui.screens.activecharging

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.components.AdditionalCostsBanner
import de.elvah.charge.features.adhoc_charging.ui.components.AdditionalCostsContent
import de.elvah.charge.features.adhoc_charging.ui.components.ChargingSessionDelayBanner
import de.elvah.charge.features.adhoc_charging.ui.model.AdditionalCostsUI
import de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart.ChargingPointErrorModal
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.ui.components.CPOLogo
import de.elvah.charge.platform.ui.components.DismissableTopAppBar
import de.elvah.charge.platform.ui.components.ErrorBanner
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.MenuItem
import de.elvah.charge.platform.ui.components.buttons.ButtonPrimary
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copyLarge
import de.elvah.charge.platform.ui.theme.copyMedium
import de.elvah.charge.platform.ui.theme.titleLargeBold
import de.elvah.charge.platform.ui.theme.titleMedium
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds


@Composable
internal fun ActiveChargingScreen(
    viewModel: ActiveChargingViewModel,
    onSupportClick: () -> Unit,
    onStopClick: () -> Unit,
    onDismissClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    when (val state = state) {
        is ActiveChargingState.Loading -> ActiveCharging_Loading()
        is ActiveChargingState.Error -> ActiveCharging_Error(state) { 
            viewModel.retry(it)
        }
        is ActiveChargingState.Success -> ActiveCharging_Success(
            state = state,
            viewModel = viewModel,
            onSupportClick = onSupportClick,
            onStopClick = {
                viewModel.stopCharging()
            },
            onBackClick = onDismissClick,
            onDismissError = {
                viewModel.onDismissError()
            }
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
private fun ActiveCharging_Error(
    state: ActiveChargingState.Error,
    onRetryClick: (SessionStatus) -> Unit,
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var showBanner by remember { mutableStateOf(true) }

            if (showBanner) {
                ErrorBanner("Error", onCloseClick = { showBanner = false })
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_bolt_off),
                    contentDescription = null,
                    tint = MaterialTheme.colorSchemeExtended.onError,
                    modifier = Modifier
                        .background(MaterialTheme.colorSchemeExtended.error, CircleShape)
                        .size(86.dp)
                        .padding(16.dp)
                )

                Spacer(modifier = Modifier.size(32.dp))
                ChargeStateTitle(state.status)
                Spacer(modifier = Modifier.size(12.dp))

                ChargeStateSubtitle(state.status)
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ButtonPrimary("Retry", modifier = Modifier.fillMaxWidth()) {

                }

                CPOLogo(state.cpoLogo, modifier = Modifier.height(50.dp))
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ActiveCharging_Success(
    state: ActiveChargingState.Success,
    viewModel: ActiveChargingViewModel,
    onSupportClick: () -> Unit,
    onStopClick: () -> Unit,
    onBackClick: () -> Unit,
    onDismissError: () -> Unit
) {
    Scaffold(
        topBar = {
            DismissableTopAppBar(
                title = stringResource(R.string.charging_session_active_title),
                onDismissClick = onBackClick,
                menuItems = listOf(
                    MenuItem(
                        text = stringResource(R.string.support_button),
                        leadingIcon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_support_agent),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        onClick = onSupportClick,
                        tint = MaterialTheme.colorScheme.primary
                    ),
                    MenuItem(
                        text = "Stop charging",
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                tint = MaterialTheme.colorSchemeExtended.onError
                            )
                        },
                        onClick = onStopClick,
                        tint = MaterialTheme.colorSchemeExtended.onError

                    )
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ChargingSessionDelayBanner(
                sessionStatus = state.activeChargingSessionUI.status,
                onStopChargingClick = {
                    viewModel.forceStopChargingAndClear()
                },
                onContactSupportClick = onSupportClick,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Spacer(Modifier.size(20.dp))

            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ChargeState(
                    state.activeChargingSessionUI.consumption,
                    state.activeChargingSessionUI.duration.seconds,
                    state.activeChargingSessionUI.status
                )
                Spacer(modifier = Modifier.size(32.dp))
                ChargeStateTitle(state.activeChargingSessionUI.status)
                Spacer(modifier = Modifier.size(12.dp))
                ChargeStateSubtitle(state.activeChargingSessionUI.status)
            }

            ActiveCharging_Success_Footer(
                logoUrl = state.activeChargingSessionUI.cpoLogo,
                additionalCostsUI = state.additionalCostsUI,
                onStopClick = onStopClick,
                onSupportClick = onSupportClick
            )

            ErrorModalSheet(
                error = state.activeChargingSessionUI.error,
                onDismissError = onDismissError
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ActiveCharging_Error_Preview() {
    ElvahChargeTheme {
        ActiveCharging_Error(state = ActiveChargingState.Error(SessionStatus.START_REJECTED, "")) { }
    }
}


@Composable
private fun ChargeStateSubtitle(status: SessionStatus, modifier: Modifier = Modifier) {
    val text = when (status) {
        SessionStatus.START_REQUESTED -> "Reaching out to the charger.\n" +
                "Please bear with us for a moment."

        SessionStatus.STARTED -> "Charger is awake!\n" +
                "Starting session with the charger."

        SessionStatus.STOP_REQUESTED -> "We are connecting to the station to end the charging session"
        SessionStatus.START_REJECTED -> "Unfortunately, the charging session could not be started at this charge point. Please try again later or use another charge point."
        SessionStatus.STOP_REJECTED -> "Please stop charging manually by removing the charging cable first from your car and then from the charging station.\n\nWe will analyze the problem and try to solve it together with the operator of the charging station."
        else -> ""
    }

    Text(
        text = text,
        style = copyMedium,
        modifier = modifier,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
private fun ChargeStateTitle(status: SessionStatus, modifier: Modifier = Modifier) {
    val text = when (status) {
        SessionStatus.START_REQUESTED -> "Preparing"
        SessionStatus.STARTED -> "Started"
        SessionStatus.STOP_REQUESTED -> "Stopping the charging session"
        SessionStatus.START_REJECTED -> "The charge point reported an error"
        SessionStatus.STOP_REJECTED -> "Please end charging manually"
        else -> ""
    }

    Text(
        text,
        style = titleMedium,
        modifier = modifier,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.primary
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ErrorModalSheet(
    error: Boolean,
    onDismissError: () -> Unit
) {
    val errorSheetState = rememberModalBottomSheetState()

    LaunchedEffect(error) {
        if (error) {
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

@Composable
private fun ActiveCharging_Success_Footer(
    logoUrl: String,
    additionalCostsUI: AdditionalCostsUI?,
    onStopClick: () -> Unit,
    onSupportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        additionalCostsUI?.let {
            AdditionalCostsLearnMoreSheet(it)
        }

        ActiveChargingActions(onStopClick, onSupportClick)

        CPOLogo(logoUrl, modifier = Modifier.height(50.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdditionalCostsLearnMoreSheet(
    additionalCostsUI: AdditionalCostsUI,
) {
    val coroutineScope = rememberCoroutineScope()
    val additionalCostsSheetState = rememberModalBottomSheetState()

    AdditionalCostsBanner(
        onLearnMoreClicked = {
            coroutineScope.launch { additionalCostsSheetState.show() }
        },
        modifier = Modifier
            .fillMaxWidth(),
    )

    if (additionalCostsSheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { /*do nothing*/ },
            sheetState = additionalCostsSheetState,
            dragHandle = null,
            scrimColor = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.32f,
            ),
            containerColor = MaterialTheme.colorScheme.background,
        ) {
            AdditionalCostsContent(
                activationFee = additionalCostsUI.activationFee,
                blockingFee = additionalCostsUI.blockingFee,
                blockingFeeMaxPrice = additionalCostsUI.blockingFeeMaxPrice,
                startsAfterMinutes = additionalCostsUI.startsAfterMinutes,
                timeSlots = additionalCostsUI.timeSlots,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun ChargeIcon(status: SessionStatus, modifier: Modifier = Modifier) {

    val size = if (status == SessionStatus.CHARGING) {
        DpSize(32.dp, 32.dp)
    } else {
        DpSize(44.dp, 44.dp)
    }

    val icon = when (status) {
        SessionStatus.CHARGING -> R.drawable.ic_bolt
        SessionStatus.START_REQUESTED -> R.drawable.ic_bolt
        SessionStatus.STARTED -> R.drawable.ic_green_tick
        SessionStatus.STOP_REQUESTED -> R.drawable.ic_bolt_off
        else -> R.drawable.ic_bolt
    }

    Icon(
        modifier = modifier.size(size),
        painter = painterResource(icon),
        contentDescription = "",
        tint = MaterialTheme.colorSchemeExtended.brand
    )
}


@Composable
private fun AnimatedKilowattsText(kilowatts: Double, modifier: Modifier = Modifier) {
    val kilowattsText = buildAnnotatedString {
        withStyle(titleLargeBold.copy(color = MaterialTheme.colorScheme.primary).toSpanStyle()) {
            append("%.3f".format(kilowatts))
        }

        withStyle(copyMedium.copy(color = MaterialTheme.colorScheme.primary).toSpanStyle()) {
            append(stringResource(R.string.kwh_label))
        }
    }

    Text(
        text = kilowattsText,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun AnimatedChargingTimeText(time: Duration, modifier: Modifier = Modifier) {
    val parsedTime = remember(time) { parseTimeComponents(time) }

    Row(modifier = modifier) {
        TimeItem(
            time = parsedTime.hours.toInt(),
        )

        Text(
            text = ":",
            textAlign = TextAlign.Center,
            style = copyLarge,
            color = MaterialTheme.colorScheme.secondary
        )

        TimeItem(
            time = parsedTime.minutes,
        )

        Text(
            text = ":",
            textAlign = TextAlign.Center,
            style = copyLarge,
            color = MaterialTheme.colorScheme.secondary
        )


        TimeItem(
            time = parsedTime.seconds,
        )
    }

}

@Composable
private fun TimeItem(time: Int, modifier: Modifier = Modifier) {

    var oldTime by remember { mutableIntStateOf(time) }

    SideEffect {
        oldTime = time
    }

    val timeString = time.toString()
    val oldTimeString = oldTime.toString()

    for (i in time.toString().indices) {
        val oldChar = oldTimeString.getOrNull(i)
        val newChar = timeString.getOrNull(i)

        val char = if (oldChar == newChar) {
            oldTimeString[i]
        } else {
            timeString[i]
        }

        if (time < 10) {
            Text(
                text = "0",
                textAlign = TextAlign.Center,
                modifier = modifier,
                style = copyLarge,
                color = MaterialTheme.colorScheme.secondary

            )
        }

        AnimatedContent(
            targetState = char.toString().toInt(),
            transitionSpec = {
                slideInVertically { it } togetherWith slideOutVertically { it }
            }
        ) {

            Text(
                text = "%d".format(it),
                textAlign = TextAlign.Center,
                modifier = modifier,
                style = copyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

private data class TimeComponents(
    val hours: Long,
    val minutes: Int,
    val seconds: Int
)

private fun parseTimeComponents(time: Duration): TimeComponents {
    return time.toComponents { hours, minutes, seconds, _ ->
        TimeComponents(
            hours = hours,
            minutes = minutes,
            seconds = seconds
        )
    }
}


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
    }
}

// Preview disabled due to ViewModel dependency complexity
// @PreviewLightDark
// @Composable
// private fun ActiveCharging_Success_Preview() {
//     ElvahChargeTheme {
//         // Preview implementation would require complex mocking
//     }
// }


@Composable
private fun ChargeState(
    kilowatts: Double,
    duration: Duration,
    status: SessionStatus,
    modifier: Modifier = Modifier
) {
    val progressPadding = 24.dp

    Layout(
        modifier = modifier.animateContentSize(),
        content = {
            // Progress indicator (index 0)
            CircularProgressIndicator(
                strokeWidth = 21.dp,
                color = MaterialTheme.colorSchemeExtended.brand
            )

            // Content (index 1)
            ChargeState_Content(kilowatts, duration, status)
        }
    ) { measurables, constraints ->
        // Measure content first
        val contentPlaceable = measurables[1].measure(constraints)

        // Calculate size for progress indicator based on content size + padding
        val progressSize = maxOf(
            contentPlaceable.width + progressPadding.roundToPx() * 2,
            contentPlaceable.height + progressPadding.roundToPx() * 2
        )

        val progressConstraints = constraints.copy(
            minWidth = progressSize,
            maxWidth = progressSize,
            minHeight = progressSize,
            maxHeight = progressSize
        )

        val progressPlaceable = measurables[0].measure(progressConstraints)

        // Layout size is the progress indicator size
        val layoutWidth = progressPlaceable.width
        val layoutHeight = progressPlaceable.height

        layout(layoutWidth, layoutHeight) {
            // Center progress indicator
            progressPlaceable.placeRelative(0, 0)

            // Center content within progress indicator
            val contentX = (layoutWidth - contentPlaceable.width) / 2
            val contentY = (layoutHeight - contentPlaceable.height) / 2
            contentPlaceable.placeRelative(contentX, contentY)
        }
    }
}

@Composable
private fun ChargeState_Content(
    kilowatts: Double,
    duration: Duration,
    status: SessionStatus,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        ChargeIcon(status)
        if (status == SessionStatus.CHARGING) {
            AnimatedVisibility(
                visible = kilowatts > 0.0,
                enter = fadeIn(animationSpec = tween(300)) + expandVertically(
                    animationSpec = tween(
                        300
                    )
                ),
                exit = fadeOut(animationSpec = tween(300)) + shrinkVertically(
                    animationSpec = tween(
                        300
                    )
                )
            ) {
                AnimatedKilowattsText(kilowatts)
            }

            AnimatedChargingTimeText(duration)
        }
    }
}

@PreviewLightDark
@Composable
private fun ChargeState_Preview() {
    ElvahChargeTheme {
        ChargeState(12.0, 97.seconds, SessionStatus.CHARGING, modifier = Modifier)
    }
}
