package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.ChargePointItemUI
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.SiteDetailState
import de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.successStateMock
import de.elvah.charge.platform.ui.components.CopyLarge
import de.elvah.charge.platform.ui.components.CopyXLarge
import de.elvah.charge.platform.ui.extension.horizontalElement
import de.elvah.charge.platform.ui.extension.verticalElement
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

@Composable
internal fun ChargePointsList(
    state: SiteDetailState.Success,
    modifier: Modifier = Modifier,
    onChargePointSearchInputChange: (String) -> Unit,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        SelectChargePointHeader(state, onChargePointSearchInputChange)

        if (state.chargePoints.isNotEmpty()) {
            ChargePointsListContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                items = state.chargePoints,
                onItemClick = onItemClick,
            )

        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                CopyLarge(stringResource(R.string.no_charge_points_available))
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
            modifier = Modifier
                .padding(
                    vertical = 12.dp,
                ),
            text = stringResource(R.string.select_charge_point_label),
            fontWeight = FontWeight.W700,
        )

        SearchChargePointInputField(
            searchInput = state.searchInput,
            onSearchInputChange = onChargePointSearchInputChange,
        )
    }
}

@Composable
private fun ChargePointsListContent(
    items: List<ChargePointItemUI>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(items) { index, item ->
            ChargePointItemWithSeparator(
                modifier = Modifier
                    .animateItem(),
                contentPadding = PaddingValues(
                    top = 17.dp,
                    bottom = 12.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
                chargePoint = item,
                showSeparator = index != items.lastIndex,
                onClick = { onItemClick(item.evseId) },
            )
        }
    }
}

@Composable
private fun ChargePointItemWithSeparator(
    chargePoint: ChargePointItemUI,
    onClick: () -> Unit,
    showSeparator: Boolean,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable(
                onClick = onClick,
            )
            .padding(contentPadding.horizontalElement()),
    ) {
        ChargePointItem(
            contentPadding = contentPadding.verticalElement,
            chargePoint = chargePoint,
        )

        if (showSeparator) {
            HorizontalDivider()
        }
    }
}

@PreviewLightDark
@Composable
private fun ChargePointsListPreview() {
    ElvahChargeTheme {
        ChargePointsList(
            state = successStateMock,
            onChargePointSearchInputChange = {},
            onItemClick = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun EmptyListPreview() {
    ElvahChargeTheme {
        ChargePointsList(
            modifier = Modifier,
            state = successStateMock.copy(
                chargePoints = listOf(),
            ),
            onChargePointSearchInputChange = {},
            onItemClick = {},
        )
    }
}
