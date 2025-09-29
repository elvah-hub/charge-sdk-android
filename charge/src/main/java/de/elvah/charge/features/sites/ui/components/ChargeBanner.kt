package de.elvah.charge.features.sites.ui.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.features.sites.ui.model.ChargeBannerRender
import de.elvah.charge.features.sites.ui.model.Location
import de.elvah.charge.features.sites.ui.utils.MockData
import de.elvah.charge.features.sites.ui.utils.parseDate
import de.elvah.charge.platform.ui.components.ButtonPrimary
import de.elvah.charge.platform.ui.components.Chevron
import de.elvah.charge.platform.ui.components.CopyMedium
import de.elvah.charge.platform.ui.components.CopySmall
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.brand
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Composable
internal fun ChargeBanner_Content(
    site: ChargeBannerRender,
    modifier: Modifier = Modifier,
    compact: Boolean = true,
    onDealClick: (ChargeBannerRender) -> Unit
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            site.originalPrice?.let {
                ChargeBannerHeader(
                    timeLeft = site.campaignEnd,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            if (compact) {
                ChargeBannerContentCollapsed(
                    price = site.price,
                    originalPrice = site.originalPrice,
                    onBannerClick = {
                        onDealClick(site)
                    }
                )
            } else {
                ChargeBannerContentExpanded(
                    price = site.price,
                    originalPrice = site.originalPrice,
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
    price: Double,
    modifier: Modifier = Modifier,
    originalPrice: Double? = null,
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
        ChargeBannerPrice(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onBannerClick()
                },
            price = price,
            originalPrice = originalPrice
        )
    }
}

@Composable
internal fun ChargeBanner_Loading(modifier: Modifier = Modifier) {
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
                stringResource(R.string.campaign_banner__deal_loading_error__text),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
internal fun ChargeBanner_ActiveSession(
    site: ChargeBannerActiveSessionRender,
    onBannerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth()) {
            ChargeBannerHeaderActiveSession(
                chargeTime = site.chargeTime,
                modifier = Modifier.fillMaxWidth()
            )
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
                    stringResource(R.string.campaign_banner__active_session_text),
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
        BestDealTimeLeft(timeLeft)
    }
}

@Composable
private fun
        ChargeBannerHeaderActiveSession(chargeTime: Duration, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(color = MaterialTheme.colorScheme.onTertiary)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CopySmall(
            text = stringResource(R.string.charging_label),
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.W700
        )

        LiveCounter(chargeTime) {
            CopySmall(
                text = it.toString(),
                color = MaterialTheme.colorScheme.brand,
                fontWeight = FontWeight.W700
            )
        }
    }
}

@Composable
private fun LiveCounter(
    initialValue: Duration,
    step: Duration = 1.seconds,
    content: @Composable (Duration) -> Unit
) {
    var counter by remember { mutableStateOf(initialValue) }

    LaunchedEffect(initialValue) {
        while (isActive) {
            delay(step)
            counter = counter.plus(step)
        }
    }

    content(counter)
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
    price: Double,
    modifier: Modifier = Modifier,
    originalPrice: Double? = null,
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
        ChargeBannerPrice(
            modifier = Modifier.fillMaxWidth(),
            price = price,
            originalPrice = originalPrice,
            collapsed = false
        )

        ButtonPrimary(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.discover_button),
            onClick = onBannerClick
        )
    }
}

@Composable
private fun ChargeBannerPrice(
    price: Double,
    modifier: Modifier = Modifier,
    originalPrice: Double? = null,
    collapsed: Boolean = false
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CopyMedium(
                stringResource(R.string.campaign_banner__ad_hoc),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W700
            )

            CopySmall(
                stringResource(R.string.campaign_banner__charge_without_registration),
            )
        }

        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            CopyMedium(
                stringResource(R.string.campaign_banner__current_price_text, price),
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.W700
            )

            originalPrice?.let {
                CopyMedium(
                    stringResource(R.string.campaign_banner__original_price_text, originalPrice),
                    textDecoration = TextDecoration.LineThrough
                )
            }
        }

        if (collapsed) {
            Chevron()
        }
    }
}

@PreviewLightDark
@Composable
private fun ChargeBannerContentExpanded_Preview() {
    ElvahChargeTheme {
        ChargeBannerPrice(
            modifier = Modifier.fillMaxWidth(),
            price = 0.24,
            originalPrice = 0.50,
            collapsed = false
        )
    }
}

@PreviewLightDark
@Composable
private fun ChargeBannerContentCollapsed_Preview() {
    ElvahChargeTheme {
        ChargeBannerPrice(
            modifier = Modifier.fillMaxWidth(),
            price = 0.24,
            originalPrice = 0.50,
            collapsed = true
        )
    }
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
            site = ChargeBannerRender(
                id = "id",
                cpoName = "Deal Title",
                address = "address",
                location = Location(
                    lat = 0.0,
                    lng = 0.0
                ),
                campaignEnd = "2025-04-23T10:21:28.405000000Z",
                originalPrice = 0.50,
                price = 0.24
            ), onDealClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ChargeBannerCollapsed_Preview() {
    ElvahChargeTheme {
        ChargeBanner_Content(
            site = MockData.chargeSiteRender,
            compact = false,
            onDealClick = {}
        )
    }
}

@PreviewLightDark
@Composable
private fun ChargeBanner_Loading_Preview() {
    ElvahChargeTheme {
        ChargeBanner_Loading()
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
            site = MockData.chargeSiteActiveSessionRender,
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
