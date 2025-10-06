package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.ChargePointsList
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.chargePointItemUIMock
import de.elvah.charge.features.sites.ui.utils.formatTimeUntil
import de.elvah.charge.platform.core.android.openMap
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.Timer
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copyMedium
import de.elvah.charge.platform.ui.theme.copyMediumBold
import de.elvah.charge.platform.ui.theme.copySmallBold
import de.elvah.charge.platform.ui.theme.titleSmallBold
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Composable
internal fun SiteDetailScreen(
    viewModel: SiteDetailViewModel,
    onCloseClick: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is SiteDetailState.Loading -> SiteDetailScreen_Loading()

        is SiteDetailState.Success -> SiteDetailScreen_Content(
            state = state,
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
    onCloseClick: () -> Unit,
    onOfferExpired: () -> Unit,
    onChargePointSearchInputChange: (String) -> Unit,
    onRefreshAvailability: () -> Unit,
    onItemClick: (String) -> Unit,
) {
    Timer(
        intervalMillis = 60_000,
        onComplete = onRefreshAvailability,
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
                discountExpiresAt = state.discountExpiresAt,
                onCloseClick = onCloseClick,
                onOfferExpired = onOfferExpired,
            )

            Spacer(Modifier.height(2.dp))

            SiteDetailHeader(state)

            Spacer(Modifier.height(16.dp))

            ChargePointsList(
                modifier = Modifier
                    .weight(1f),
                state = state,
                onChargePointSearchInputChange = onChargePointSearchInputChange,
                onItemClick = onItemClick
            )

            @Suppress("ConstantConditionIf")
            if (false) {
                Spacer(Modifier.height(36.dp))

                UnderlinedButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    text = "Back to map",
                    onClick = {}
                )

                Spacer(Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun SiteDetailTopBar(
    discountExpiresAt: LocalDateTime?,
    onCloseClick: () -> Unit,
    onOfferExpired: () -> Unit,
) {
    val discount = discountExpiresAt
        ?.let { formatTimeUntil(it) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        discount?.let { (initialFormattedTime, initialDuration) ->
            var formattedTime by remember { mutableStateOf(initialFormattedTime) }
            var duration by remember { mutableStateOf(initialDuration) }

            LaunchedEffect(duration) {
                duration?.let {
                    while (true) {
                        delay(it.inWholeSeconds)

                        val result = formatTimeUntil(discountExpiresAt)

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
                modifier = Modifier
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
                    text = "Offer ends in $formattedTime", // TODO: extract string resource
                    style = copySmallBold,
                    color = MaterialTheme.colorSchemeExtended.brand,
                    textAlign = TextAlign.Center
                )
            }
        }

        CloseButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(
                    paddingValues = if (discount != null) {
                        PaddingValues(end = 14.dp, top = 16.dp)
                    } else {
                        PaddingValues(end = 14.dp)
                    },
                ),
            onClick = onCloseClick,
        )
    }
}

@Composable
private fun CloseButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorSchemeExtended.decorativeStroke,
                shape = CircleShape
            )
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape,
            )
            .clip(shape = CircleShape)
            .clickable(
                onClick = onClick,
            )
            .padding(
                all = 10.dp
            ),
    ) {
        Icon(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.Center),
            painter = painterResource(id = R.drawable.ic_close),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = null
        )
    }
}

@Composable
private fun SiteDetailHeader(
    state: SiteDetailState.Success,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
            ),
    ) {
        Text(
            text = state.operatorName,
            color = MaterialTheme.colorScheme.primary,
            style = titleSmallBold
        )

        state.address?.let {
            Row(
                modifier = Modifier
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable {
                            context.openMap(
                                lat = state.coordinates.first,
                                lng = state.coordinates.second,
                                title = state.operatorName,
                            )
                        },
                    text = it,
                    color = MaterialTheme.colorScheme.secondary,
                    style = copyMedium,
                    textDecoration = TextDecoration.Underline,
                )

                Spacer(Modifier.width(4.dp))

                Icon(
                    modifier = Modifier
                        .size(16.dp),
                    painter = painterResource(R.drawable.ic_open_external),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null,
                )
            }
        }
    }
}

@Composable
internal fun UnderlinedButton(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clickable(
                onClick = onClick,
            )
            .width(IntrinsicSize.Max),
    ) {
        Text(
            text = text,
            style = copyMediumBold,
            color = MaterialTheme.colorScheme.primary,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(4.dp))

        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth(),
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@PreviewLightDark
@Composable
private fun SiteDetailScreen_Content_Preview() {
    ElvahChargeTheme {
        SiteDetailScreen_Content(
            state = successStateMock,
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
