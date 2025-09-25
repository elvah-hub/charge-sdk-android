package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.wrapContentWidth
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
import de.elvah.charge.platform.ui.theme.titleSmallBold

@Composable
internal fun SiteDetailScreen(
    viewModel: SiteDetailViewModel,
    onItemClick: (String) -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is SiteDetailState.Loading -> SiteDetailScreen_Loading()

        is SiteDetailState.Success -> SiteDetailScreen_Content(
            state = state,
            onChargePointSearchInputChange = viewModel::onChargePointSearchInputChange,
            onItemClick = onItemClick
        )

        is SiteDetailState.Error -> SiteDetailScreen_Error()
    }
}

@Composable
private fun SiteDetailScreen_Content(
    state: SiteDetailState.Success,
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
            SiteDetailHeader(state)

            Spacer(Modifier.height(16.dp))

            SelectChargePointHeader(state, onChargePointSearchInputChange)

            Spacer(Modifier.height(16.dp))

            ChargePointsList(state.chargeSiteUI.chargePoints, onItemClick = onItemClick)
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

@PreviewLightDark
@Composable
private fun SiteDetailScreen_Content_Preview() {
    ElvahChargeTheme {
        SiteDetailScreen_Content(
            SiteDetailState.Success(
                searchInput = "",
                chargeSiteUI = MockData.siteUI,
            ),
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
