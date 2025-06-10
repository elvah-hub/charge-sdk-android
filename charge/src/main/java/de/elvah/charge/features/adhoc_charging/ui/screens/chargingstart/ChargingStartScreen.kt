package de.elvah.charge.features.adhoc_charging.ui.screens.chargingstart

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R

import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts
import de.elvah.charge.platform.ui.components.BasicCard
import de.elvah.charge.platform.ui.components.ButtonPrimary
import de.elvah.charge.platform.ui.components.ButtonTertiary
import de.elvah.charge.platform.ui.components.CPOLogo
import de.elvah.charge.platform.ui.components.CopyLarge
import de.elvah.charge.platform.ui.components.ElvahLogo
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.OrderedList
import de.elvah.charge.platform.ui.components.SwipeButton
import de.elvah.charge.platform.ui.components.TickBanner
import de.elvah.charge.platform.ui.components.TitleMedium
import de.elvah.charge.platform.ui.components.TitleSmall
import de.elvah.charge.platform.ui.theme.brand
import de.elvah.charge.platform.ui.theme.onBrand
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
            state as ChargingStartState.Success,
            onStartCharging = onStartCharging,
            onCloseBanner = { viewModel.closeBanner() }
        )
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
) {
    val sheetState = rememberModalBottomSheetState()
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
            ) {
                Spacer(Modifier.size(30.dp))

                CPOLogo(state.organisationDetails.logoUrl)

                Spacer(Modifier.size(40.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    ConnectVehicleMessage(Modifier.padding(horizontal = 70.dp))

                    Spacer(modifier = Modifier.size(70.dp))

                    ChargingIdBadge(state.evseId)

                    ButtonTertiary(stringResource(R.string.is_charging_label)) {
                        scope.launch {
                            sheetState.show()
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                ChargingActions(onStartCharging = onStartCharging)

                Spacer(Modifier.size(20.dp))
                ElvahLogo()
            }

            if (state.shouldShowAuthorizationBanner) {
                TickBanner(
                    stringResource(R.string.authorization_successful),
                    onCloseClick = onCloseBanner,
                    modifier = Modifier.padding(16.dp),
                )
            }
        }

        if (sheetState.isVisible) {
            ModalBottomSheet(onDismissRequest = {
                scope.launch {
                    sheetState.hide()
                }
            }, sheetState = sheetState) {
                ChargingPointLockedModal {
                    scope.launch {
                        sheetState.hide()
                    }
                }
            }
        }
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

@Preview
@Composable
private fun ChargingPointLockedModal_Preview() {
    ChargingPointLockedModal {}
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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TitleSmall(
            text = stringResource(R.string.connect_vehicle),
            fontWeight = FontWeight.W700,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ChargingIdBadge(id: String, modifier: Modifier = Modifier) {
    BasicCard(
        modifier = modifier,
        backgroundColor = MaterialTheme.colorScheme.brand,
        paddingValues = PaddingValues(10.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plug),
                tint = MaterialTheme.colorScheme.onBrand,
                contentDescription = null
            )
            Text(
                id,
                fontSize = 18.sp,
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.onBrand
            )
        }
    }
}

@Preview
@Composable
private fun ChargingStart_Success_Preview() {
    ChargingStart_Success(
        state = ChargingStartState.Success(
            evseId = "",
            organisationDetails = OrganisationDetails(
                "",
                "",
                "",
                "",
                supportContacts = SupportContacts()

            )
        ), onStartCharging = {}, onCloseBanner = {})
}