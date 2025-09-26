package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.ChargePointsList
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.SearchChargePointInputField
import de.elvah.charge.features.sites.ui.utils.MockData
import de.elvah.charge.platform.core.android.openMap
import de.elvah.charge.platform.ui.components.CopyXLarge
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.brand
import de.elvah.charge.platform.ui.theme.copyMediumBold
import de.elvah.charge.platform.ui.theme.copySmallBold
import de.elvah.charge.platform.ui.theme.titleSmallBold
import kotlinx.datetime.LocalDateTime

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
            onChargePointSearchInputChange = viewModel::onChargePointSearchInputChange,
            onItemClick = onItemClick
        )

        is SiteDetailState.Error -> SiteDetailScreen_Error()
    }
}

@Composable
private fun SiteDetailScreen_Content(
    state: SiteDetailState.Success,
    onCloseClick: () -> Unit,
    onChargePointSearchInputChange: (String) -> Unit,
    onItemClick: (String) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.displayCutout
            .union(WindowInsets.systemBars),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {

            OfferBannerAndClose(null, onCloseClick)

            Spacer(Modifier.height(2.dp))

            SiteDetailHeader(state)

            Spacer(Modifier.height(16.dp))

            SelectChargePointHeader(state, onChargePointSearchInputChange)

            Spacer(Modifier.height(16.dp))

            ChargePointsList(
                modifier = Modifier
                    .weight(1f),
                chargePoints = state.chargeSiteUI.chargePoints,
                onItemClick = onItemClick
            )

            @Suppress("ConstantConditionIf")
            if (false) {
                Spacer(Modifier.height(36.dp))

                Button(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally),
                    onClick = {}
                )

                Spacer(Modifier.height(36.dp))
            }
        }
    }
}

@Composable
private fun OfferBannerAndClose(
    @Suppress("SameParameterValue")
    offerEndDateTime: LocalDateTime?,
    onCloseClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        offerEndDateTime?.let {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.brand.copy(
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
                    text = "Offer ends in 13h 11m",
                    style = copySmallBold,
                    color = MaterialTheme.colorScheme.brand,
                    textAlign = TextAlign.Center
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(
                    end = 12.dp,
                    top = 16.dp,
                )
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape,
                )
                .clickable(
                    onClick = onCloseClick,
                )
                .padding(
                    all = 10.dp
                ),
        ) {
            Icon(
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.Center),
                painter = painterResource(id = R.drawable.ic_close),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
        }
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
            text = state.chargeSiteUI.cpoName,
            color = MaterialTheme.colorScheme.primary,
            style = titleSmallBold
        )

        state.chargeSiteUI.address
            .takeIf { it.isNotBlank() }
            ?.let {
                Row(
                    modifier = Modifier
                        .padding(top = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clickable {
                                with(state.chargeSiteUI) {
                                    context.openMap(lat, lng, cpoName)
                                }
                            },
                        text = it,
                        color = MaterialTheme.colorScheme.secondary,
                        textDecoration = TextDecoration.Underline,
                    )

                    Spacer(Modifier.height(16.dp))

                    Icon(
                        painter = painterResource(R.drawable.ic_open_external),
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = null,
                    )
                }
            }
    }
}

@Composable
private fun SelectChargePointHeader(
    state: SiteDetailState.Success,
    onChargePointSearchInputChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(
                horizontal = 16.dp,
            ),
    ) {
        CopyXLarge(
            text = stringResource(R.string.select_charge_point_label),
            fontWeight = FontWeight.W700,
        )

        Spacer(Modifier.height(16.dp))

        SearchChargePointInputField(
            searchInput = state.searchInput,
            onSearchInputChange = onChargePointSearchInputChange,
        )
    }
}

@Composable
private fun Button(
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
            text = "Back to map",
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
            SiteDetailState.Success(
                searchInput = "",
                chargeSiteUI = MockData.siteUI,
            ),
            onCloseClick = {},
            onChargePointSearchInputChange = {},
            onItemClick = { _ -> },
        )
    }
}

@PreviewLightDark
@Composable
private fun SiteDetailScreen_Content_EmptyList_Preview() {
    ElvahChargeTheme {
        SiteDetailScreen_Content(
            SiteDetailState.Success(
                searchInput = "",
                chargeSiteUI = MockData.siteWithoutChargePoints,
            ),
            onCloseClick = {},
            onChargePointSearchInputChange = {},
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
