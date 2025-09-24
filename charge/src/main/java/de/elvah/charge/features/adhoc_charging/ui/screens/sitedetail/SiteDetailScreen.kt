package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist.ChargePointsList
import de.elvah.charge.features.sites.ui.utils.MockData
import de.elvah.charge.platform.core.android.openMap
import de.elvah.charge.platform.ui.components.BasicCard
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
        is SiteDetailState.Success -> SiteDetailScreen_Content(state, onItemClick)
        is SiteDetailState.Error -> SiteDetailScreen_Error()
    }
}

@Composable
private fun SiteDetailScreen_Content(
    state: SiteDetailState.Success,
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

                Spacer(modifier = Modifier.size(24.dp))

                CopyXLarge(
                    text = stringResource(R.string.select_charge_point_label),
                    fontWeight = FontWeight.W700,
                )
                Spacer(modifier = Modifier.size(10.dp))

                OfferBanner(modifier = Modifier.fillMaxWidth())
            }

            ChargePointsList(state.chargeSiteUI.chargePoints, onItemClick = onItemClick)
        }
    }
}

@Composable
private fun OfferBanner(modifier: Modifier = Modifier) {
    BasicCard {
        Row(
            modifier = modifier
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painterResource(R.drawable.ic_discount),
                null
            )
            CopyMedium(
                stringResource(R.string.charging_discont_banner_label),
                fontWeight = FontWeight.W700
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun SiteDetailScreen_Content_Preview() {
    ElvahChargeTheme {
        SiteDetailScreen_Content(
            SiteDetailState.Success(
                chargeSiteUI = MockData.siteUI
            ),
            onItemClick = { _ -> }
        )
    }
}

@PreviewLightDark
@Composable
private fun SiteDetailScreen_Content_EmptyList_Preview() {
    ElvahChargeTheme {
        SiteDetailScreen_Content(
            SiteDetailState.Success(
                chargeSiteUI = MockData.siteWithoutChargePoints
            ),
            onItemClick = { _ -> }
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
