package de.elvah.charge.features.adhoc_charging.ui.components

import android.widget.Space
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.platform.simulator.data.repository.SessionStatus
import de.elvah.charge.platform.ui.components.buttons.ButtonPrimary
import de.elvah.charge.platform.ui.components.buttons.ButtonTertiary
import de.elvah.charge.platform.ui.components.buttons.SecondaryButton
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copyLargeBold
import de.elvah.charge.platform.ui.theme.copyMedium
import kotlinx.coroutines.delay

@Composable
internal fun ChargingSessionDelayBanner(
    sessionStatus: SessionStatus,
    onStopChargingClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    modifier: Modifier = Modifier,
    delayTimeSeconds: Long = 30
) {
    val shouldShowBanner = sessionStatus in listOf(
        SessionStatus.STARTED,
        SessionStatus.START_REQUESTED,
        SessionStatus.STOP_REQUESTED
    )

    var isVisible by remember(sessionStatus) {
        mutableStateOf(false)
    }

    LaunchedEffect(sessionStatus, shouldShowBanner) {
        if (shouldShowBanner) {
            delay(delayTimeSeconds * 1000)
            isVisible = true
        } else {
            isVisible = false
        }
    }

    AnimatedVisibility(
        visible = isVisible && shouldShowBanner,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        ChargingDelayBannerContent(
            onStopChargingClick = onStopChargingClick,
            onContactSupportClick = onContactSupportClick,
            modifier = modifier
        )
    }

    if (!isVisible){
        Spacer(Modifier.size(20.dp))
    }
}

@Composable
private fun ChargingDelayBannerContent(
    onStopChargingClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp),
        ) {
            BannerHeader()
            Spacer(Modifier.size(12.dp))
            Text(
                text = stringResource(R.string.charging_delay_banner_description),
                style = copyMedium,
                modifier = modifier.padding(horizontal = 16.dp),
                color = MaterialTheme.colorScheme.secondary
            )
            BannerActions(
                onStopChargingClick = onStopChargingClick,
                onContactSupportClick = onContactSupportClick
            )
        }
    }
}

@Composable
private fun BannerHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_support_agent),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = stringResource(R.string.charging_delay_banner_title),
                style = copyLargeBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun BannerActions(
    onStopChargingClick: () -> Unit,
    onContactSupportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ButtonTertiary(
            text = stringResource(R.string.contact_support_button),
            onClick = onContactSupportClick
        )

        ButtonTertiary(
            text = stringResource(R.string.stop_charging_button),
            onClick = onStopChargingClick
        )
    }
}

@PreviewLightDark
@Composable
private fun ChargingSessionDelayBanner_Preview() {
    ElvahChargeTheme {
        ChargingDelayBannerContent(
            onStopChargingClick = {},
            onContactSupportClick = {}
        )
    }
}
