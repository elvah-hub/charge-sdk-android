package de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.components.ShortenedEvseBigBadge
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts
import de.elvah.charge.platform.ui.components.CPOLogo
import de.elvah.charge.platform.ui.components.CopyLarge
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.OrderedList
import de.elvah.charge.platform.ui.components.TickBanner
import de.elvah.charge.platform.ui.components.TitleMedium
import de.elvah.charge.platform.ui.components.buttons.ButtonPrimary
import de.elvah.charge.platform.ui.components.buttons.ButtonTertiary
import de.elvah.charge.platform.ui.components.buttons.SwipeButton
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.secondary
import de.elvah.charge.platform.ui.theme.copyMedium
import kotlinx.coroutines.launch

@Composable
internal fun ChargingStartScreen(
    viewModel: ChargingStartViewModel,
    onStartCharging: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (state) {
        is ChargingStartState.Loading -> ChargingStart_Loading()
        is ChargingStartState.Error -> ChargingStart_Error()
        is ChargingStartState.Success -> ChargingStart_Success(
            state = state as ChargingStartState.Success,
            onStartCharging = { viewModel.startChargeSession() },
            onCloseBanner = { viewModel.closeBanner() },
            onDismissError = { viewModel.onDismissError() }
        )

        ChargingStartState.StartRequest -> {
            SideEffect {
                onStartCharging()
            }
        }
    }
}

@Composable
internal fun ChargingStart_Loading() {
    FullScreenLoading()
}

@Composable
internal fun ChargingStart_Error() {
    FullScreenError()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChargingStart_Success(
    state: ChargingStartState.Success,
    onStartCharging: () -> Unit,
    onCloseBanner: () -> Unit,
    onDismissError: () -> Unit,
) {
    val lockedSheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold {
        Box(
            Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .padding(top = 100.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                Spacer(Modifier.size(10.dp))

                StartCharging_Success_Content(
                    evseId = state.evseId,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    scope.launch {
                        lockedSheetState.show()
                    }
                }

                StartCharging_Success_Footer(
                    logoUrl = state.organisationDetails.logoUrl,
                    onStartCharging = onStartCharging
                )
            }

            if (state.shouldShowAuthorizationBanner) {
                TickBanner(
                    text = stringResource(R.string.authorization_successful),
                    onCloseClick = onCloseBanner,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        if (lockedSheetState.isVisible) {
            ModalBottomSheet(
                onDismissRequest = {
                    scope.launch {
                        lockedSheetState.hide()
                    }
                }, sheetState = lockedSheetState
            ) {
                ChargingPointLockedModal {
                    scope.launch {
                        lockedSheetState.hide()
                    }
                }
            }
        }
        val errorSheetState = rememberModalBottomSheetState()

        LaunchedEffect(state.error) {
            if (state.error) {
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

@Composable
private fun StartCharging_Success_Content(
    evseId: String,
    modifier: Modifier = Modifier,
    onLockedClicked: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ConnectVehicleMessage(Modifier.padding(horizontal = 45.dp))

        Spacer(modifier = Modifier.size(22.dp))

        ShortenedEvseBigBadge(evseId)

        Spacer(modifier = Modifier.size(10.dp))

        ButtonTertiary(stringResource(R.string.is_charging_label), onClick = onLockedClicked)
    }
}


@Composable
private fun StartCharging_Success_Footer(
    logoUrl: String,
    modifier: Modifier = Modifier,
    onStartCharging: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ChargingActions(onStartCharging = onStartCharging)
        CPOLogo(logoUrl, modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun ChargingPointLockedModal(
    modifier: Modifier = Modifier,
    onCloseModal: () -> Unit,
) {
    Column(
        modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        CopyLarge(
            stringResource(R.string.charge_label),
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(16.dp))
        TitleMedium(
            stringResource(R.string.start_your_session_to_unlock),
            fontWeight = FontWeight.W700
        )

        Spacer(modifier = Modifier.size(16.dp))
        OrderedList(
            listOf(
                stringResource(R.string.help_unlock_item_1),
                stringResource(R.string.help_unlock_item_2)
            )
        )

        Spacer(modifier = Modifier.size(16.dp))

        ButtonPrimary("Close", modifier = Modifier.fillMaxWidth(), onClick = onCloseModal)
    }
}

@PreviewLightDark
@Composable
private fun ChargingPointLockedModal_Preview() {
    ElvahChargeTheme {
        ChargingPointLockedModal {}
    }
}

@Composable
internal fun ChargingPointErrorModal(
    modifier: Modifier = Modifier,
    onCloseModal: () -> Unit,
) {
    Column(
        modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp)
    ) {
        CopyLarge(
            text = stringResource(R.string.generic_error_bottom_sheet__title),
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.size(16.dp))

        ButtonPrimary(
            stringResource(R.string.understood),
            modifier = Modifier.fillMaxWidth(),
            onClick = onCloseModal
        )
    }
}

@PreviewLightDark
@Composable
private fun ChargingPointErrorModal_Preview() {
    ElvahChargeTheme {
        ChargingPointErrorModal { }
    }
}


@Composable
private fun ChargingActions(modifier: Modifier = Modifier, onStartCharging: () -> Unit) {
    Column(modifier) {
        SwipeButton(stringResource(R.string.start_charging_process)) {
            onStartCharging()
        }
    }
}

@Composable
private fun ConnectVehicleMessage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TitleMedium(
            text = stringResource(R.string.charge_start__title),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.connect_vehicle),
            textAlign = TextAlign.Center,
            style = copyMedium,
            color = secondary
        )
    }
}

@PreviewLightDark
@Composable
private fun ChargingStart_Success_Preview() {
    ElvahChargeTheme {
        ChargingStart_Success(
            state = ChargingStartState.Success(
                evseId = "DE*01",
                organisationDetails = OrganisationDetails(
                    "",
                    "",
                    "",
                    "",
                    supportContacts = SupportContacts()
                )
            ),
            onStartCharging = {},
            onCloseBanner = {},
            onDismissError = {}
        )
    }
}
