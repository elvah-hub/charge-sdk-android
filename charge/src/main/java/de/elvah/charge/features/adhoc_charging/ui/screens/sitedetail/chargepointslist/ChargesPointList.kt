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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.sites.ui.model.ChargePointUI
import de.elvah.charge.platform.ui.components.CopyLarge
import de.elvah.charge.platform.ui.extension.horizontalElement
import de.elvah.charge.platform.ui.extension.verticalElement
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

@Composable
internal fun ChargePointsList(
    chargePoints: List<ChargePointUI>,
    modifier: Modifier = Modifier,
    onItemClick: (String) -> Unit
) {
    Column(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (chargePoints.isNotEmpty()) {
            ChargePointsListContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                items = chargePoints,
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
private fun ChargePointsListContent(
    modifier: Modifier = Modifier,
    items: List<ChargePointUI>,
    onItemClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(items) { index, item ->
            ChargePointItemWithSeparator(
                contentPadding = PaddingValues(
                    top = 17.dp,
                    bottom = 12.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
                chargePoint = item,
                showSeparator = index != items.lastIndex,
                onClick = {
                    onItemClick(item.shortenedEvseId)
                },
            )
        }
    }
}

@Composable
private fun ChargePointItemWithSeparator(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    chargePoint: ChargePointUI,
    onClick: () -> Unit,
    showSeparator: Boolean,
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
            chargePoints = chargePointsMock,
            onItemClick = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun EmptyListPreview() {
    ElvahChargeTheme {
        ChargePointsList(
            chargePoints = listOf(),
            onItemClick = {},
        )
    }
}

private val chargePointsMock = listOf(
    chargePointUIMock,
    chargePointUIMock,
    chargePointUIMock,
    chargePointUIMock,
)