package de.elvah.charge.features.deals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.R
import de.elvah.charge.features.deals.ui.utils.MockData
import de.elvah.charge.features.deals.ui.utils.parseDate
import de.elvah.charge.features.sites.ui.model.ChargeSiteUI
import de.elvah.charge.platform.ui.components.ButtonPrimary
import de.elvah.charge.platform.ui.components.Chevron
import de.elvah.charge.platform.ui.components.CopyMedium
import de.elvah.charge.platform.ui.components.CopySmall
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.brand

@Composable
internal fun ChargeBanner_Content(
    site: ChargeSiteUI,
    modifier: Modifier = Modifier,
    compact: Boolean = true,
    onDealClick: (ChargeSiteUI) -> Unit
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChargeBannerHeader(site.campaignEnd, modifier = Modifier.fillMaxWidth())
            if (compact) {
                ChargeBannerContentCollapsed(
                    operatorName = site.cpoName,
                    price = site.pricePerKw,
                    onBannerClick = {
                        onDealClick(site)
                    }
                )
            } else {
                ChargeBannerContentExpanded(
                    operatorName = site.cpoName,
                    price = site.pricePerKw,
                    onBannerClick = {
                        onDealClick(site)
                    }
                )
            }
        }
    }
}

@Composable
private fun ChargeBannerContentCollapsed(
    operatorName: String,
    price: Double,
    modifier: Modifier = Modifier,
    onBannerClick: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(vertical = 16.dp)
            .padding(start = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        ChargeBannerPrice(operatorName, price, modifier = Modifier.weight(1f))

        IconButton(onClick = onBannerClick) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null
            )
        }
    }
}

@Composable
internal fun DealBanner_Loading(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@Composable
internal fun ChargeBanner_Error(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.error_deal_banner),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
internal fun ChargeBanner_ActiveSession(
    site: ChargeSiteUI,
    onBannerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChargeBannerHeader(site.campaignEnd, modifier = Modifier.fillMaxWidth())

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.tertiary)
                    .padding(vertical = 16.dp)
                    .padding(start = 16.dp)
                    .clickable { onBannerClick(site.id) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CopyMedium(
                    stringResource(R.string.active_session_label),
                    modifier = Modifier.weight(1f)
                )

                Chevron()
            }
        }
    }
}

@Composable
private fun ChargeBannerHeader(timeLeft: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.onTertiary)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        BestDealLabel()
        BestDealTimeLeft(timeLeft)
    }
}

@Composable
private fun BestDealLabel(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_pinpoint),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        CopySmall(
            stringResource(R.string.best_deal_around_you_label),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.W700
        )
    }
}

@Composable
private fun BestDealTimeLeft(timeLeft: String, modifier: Modifier = Modifier) {
    CopySmall(
        text = parseDate(timeLeft),
        modifier = modifier,
        color = MaterialTheme.colorScheme.brand,
        fontWeight = FontWeight.W700
    )
}

@Composable
private fun ChargeBannerContentExpanded(
    operatorName: String,
    price: Double,
    modifier: Modifier = Modifier,
    onBannerClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ChargeBannerPrice(operatorName, price)

        ButtonPrimary(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.discover_button),
            onClick = onBannerClick
        )
    }
}

@Composable
private fun ChargeBannerPrice(operatorName: String, price: Double, modifier: Modifier = Modifier) {
    val buildText = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.primary
            )
        ) {
            append(stringResource(R.string.charge_at_station_placeholder, operatorName))
        }

        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.brand
            )
        ) {
            append(stringResource(R.string.euros_kw_label, price))
        }
    }
    Text(buildText, modifier = modifier)
}

@Composable
internal fun ChargeBanner_Empty(modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                stringResource(R.string.campaign_banner__no_data_text),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}


@PreviewLightDark
@Composable
private fun ChargeBanner_Preview() {
    ElvahChargeTheme {
        ChargeBanner_Content(
            ChargeSiteUI(
                id = "id",
                cpoName = "Deal Title",
                pricePerKw = 100.0,
                campaignEnd = "2025-04-23T10:21:28.405000000Z",
                address = "address",
                lat = 0.0,
                lng = 0.0,
                chargePoints = emptyList()
            ), onDealClick = {})
    }
}

@PreviewLightDark
@Composable
private fun ChargeBannerCollapsed_Preview() {
    ElvahChargeTheme {
        ChargeBanner_Content(
            ChargeSiteUI(
                id = "id",
                cpoName = "Deal Title",
                pricePerKw = 100.0,
                campaignEnd = "2025-04-23T10:21:28.405000000Z",
                address = "address",
                lat = 0.0,
                lng = 0.0,
                chargePoints = emptyList()
            ), compact = false, onDealClick = {})
    }
}

@PreviewLightDark
@Composable
private fun ChargeBanner_Loading_Preview() {
    ElvahChargeTheme {
        DealBanner_Loading()
    }
}

@PreviewLightDark
@Composable
private fun ChargeBanner_Error_Preview() {
    ElvahChargeTheme {
        ChargeBanner_Error()
    }
}

@PreviewLightDark
@Composable
private fun ChargeBanner_ActiveSession_Preview() {
    ElvahChargeTheme {
        ChargeBanner_ActiveSession(
            site = MockData.siteUI,
            onBannerClick = {})
    }
}

@PreviewLightDark
@Composable
private fun ChargeBanner_Empty_Preview() {
    ElvahChargeTheme {
        ChargeBanner_Empty()
    }
}
