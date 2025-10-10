package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.BuildConfig
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.domain.service.charge.ChargingSessionState
import de.elvah.charge.features.adhoc_charging.ui.components.ChargeSessionBanner
import de.elvah.charge.features.adhoc_charging.ui.components.OfferCounterBanner
import de.elvah.charge.features.adhoc_charging.ui.components.button.CircularIconButton
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.ChargePointsList
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.chargePointItemUIMock
import de.elvah.charge.features.sites.ui.utils.formatTimeUntil
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.Timer
import de.elvah.charge.platform.ui.components.site.SiteDetailHeader
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun SiteDetailScreen(
    viewModel: SiteDetailViewModel,
    onCloseClick: () -> Unit,
    navigateToChargeSession: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val chargeSessionState by viewModel.chargeSessionState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is SiteDetailState.Loading -> SiteDetailScreen_Loading()

        is SiteDetailState.Success -> SiteDetailScreen_Content(
            state = state,
            chargeSessionState = chargeSessionState,
            onChargeSessionActiveClick = navigateToChargeSession,
            onCloseClick = onCloseClick,
            onOfferExpired = viewModel::updateTimeSlot,
            onChargePointSearchInputChange = viewModel::onChargePointSearchInputChange,
            onRefreshAvailability = viewModel::refreshAvailability,
            onItemClick = onItemClick,
        )

        is SiteDetailState.Error -> SiteDetailScreen_Error()
    }
}

@Composable
private fun SiteDetailScreen_Content(
    state: SiteDetailState.Success,
    chargeSessionState: ChargingSessionState,
    onChargeSessionActiveClick: () -> Unit,
    onCloseClick: () -> Unit,
    onOfferExpired: () -> Unit,
    onChargePointSearchInputChange: (String) -> Unit,
    onRefreshAvailability: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    Timer(
        intervalMillis = 60_000,
        onTick = onRefreshAvailability,
    )

    Scaffold(
        contentWindowInsets = WindowInsets.displayCutout
            .union(WindowInsets.systemBars),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            SiteDetailTopBar(
                chargeSessionState = chargeSessionState,
                discountExpiresAt = state.discountExpiresAt,
                onChargeSessionActiveClick = onChargeSessionActiveClick,
                onCloseClick = onCloseClick,
                onOfferExpired = onOfferExpired,
            )

            Spacer(Modifier.height(2.dp))

            SiteDetailHeader(
                operatorName = state.operatorName,
                address = state.address,
                coordinates = state.coordinates,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(16.dp))

            ChargePointsList(
                modifier = Modifier
                    .weight(1f),
                state = state,
                onChargePointSearchInputChange = onChargePointSearchInputChange,
                onItemClick = onItemClick
            )
        }
    }
}

@Composable
private fun SiteDetailTopBar(
    chargeSessionState: ChargingSessionState,
    discountExpiresAt: LocalDateTime?,
    onChargeSessionActiveClick: () -> Unit,
    onCloseClick: () -> Unit,
    onOfferExpired: () -> Unit,
) {
    val context = LocalContext.current

    val discount = discountExpiresAt
        ?.let { formatTimeUntil(context, it) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (BuildConfig.DEBUG) {
            Column {
                if (chargeSessionState.isSessionActive) {
                    ChargeSessionBanner(
                        isSummaryReady = chargeSessionState.isSessionSummaryReady,
                        onClick = onChargeSessionActiveClick,
                    )
                }

                discount?.let {
                    OfferCounterBanner(
                        discountExpiresAt = discountExpiresAt,
                        onOfferExpired = onOfferExpired,
                    )
                }
            }
        }

        CircularIconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(
                    paddingValues = if (chargeSessionState.isSessionActive || discount != null) {
                        PaddingValues(end = 14.dp, top = 16.dp)
                    } else {
                        PaddingValues(end = 14.dp)
                    },
                ),
            iconResId = R.drawable.ic_close,
            onClick = onCloseClick,
        )
    }
}


@PreviewLightDark
@Composable
private fun SiteDetailScreen_Content_Preview() {
    ElvahChargeTheme {
        SiteDetailScreen_Content(
            state = successStateMock,
            chargeSessionState = chargeIndicatorMock,
            onChargeSessionActiveClick = {},
            onCloseClick = {},
            onOfferExpired = {},
            onChargePointSearchInputChange = {},
            onRefreshAvailability = {},
            onItemClick = { _ -> },
        )
    }
}

@PreviewLightDark
@Composable
private fun SiteDetailScreen_Content_NoDiscount_Preview() {
    ElvahChargeTheme {
        SiteDetailScreen_Content(
            state = successStateMock.copy(
                discountExpiresAt = null,
            ),
            chargeSessionState = chargeIndicatorMock,
            onChargeSessionActiveClick = {},
            onCloseClick = {},
            onOfferExpired = {},
            onChargePointSearchInputChange = {},
            onRefreshAvailability = {},
            onItemClick = { _ -> },
        )
    }
}

@Composable
private fun SiteDetailScreen_Loading() {
    FullScreenLoading()
}

@Composable
private fun SiteDetailScreen_Error() {
    FullScreenError()
}

private val chargeIndicatorMock = ChargingSessionState(
    isSessionRunning = true,
    isSessionSummaryReady = false,
    lastSessionData = null,
)

private val chargePointsMock = listOf(
    chargePointItemUIMock,
    chargePointItemUIMock,
    chargePointItemUIMock,
    chargePointItemUIMock,
)

@OptIn(ExperimentalTime::class)
internal val successStateMock = SiteDetailState.Success(
    discountExpiresAt = Clock.System.now()
        .toLocalDateTime(TimeZone.currentSystemDefault()).let {
            LocalDateTime(
                year = it.year,
                month = it.month,
                day = it.day,
                hour = it.hour,
                minute = it.minute + 1,
                second = it.second,
            )
        },
    operatorName = "Lidl Köpenicker Straße",
    address = "Köpenicker Straße 145 12683 Berlin",
    coordinates = Pair(0.0, 0.0),
    searchInput = "",
    chargePoints = chargePointsMock,
    noSearchResults = false,
    noStations = false,
)
