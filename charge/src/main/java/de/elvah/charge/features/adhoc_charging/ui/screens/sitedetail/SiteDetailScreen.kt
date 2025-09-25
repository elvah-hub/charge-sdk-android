package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.ChargePointsList
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.SearchChargePointInputField
import de.elvah.charge.features.sites.ui.utils.MockData
import de.elvah.charge.platform.core.android.openMap
import de.elvah.charge.platform.ui.components.ButtonPrimary
import de.elvah.charge.platform.ui.components.CopyMedium
import de.elvah.charge.platform.ui.components.CopyXLarge
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.TitleSmall
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

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
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .systemBarsPadding()
                    .padding(16.dp)
            ) {
                TitleSmall(state.chargeSiteUI.cpoName)
                Spacer(modifier = Modifier.size(6.dp))
                CopyMedium(state.chargeSiteUI.address, color = MaterialTheme.colorScheme.secondary)

                Spacer(modifier = Modifier.size(20.dp))

                val context = LocalContext.current

                ButtonPrimary(
                    text = stringResource(R.string.route_label),
                    icon = R.drawable.ic_directions,
                    onClick = {
                        with(state.chargeSiteUI) {
                            context.openMap(lat, lng, cpoName)
                        }
                    }
                )
            }

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

            Spacer(Modifier.height(8.dp))

            ChargePointsList(state.chargeSiteUI.chargePoints, onItemClick = onItemClick)
        }
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
