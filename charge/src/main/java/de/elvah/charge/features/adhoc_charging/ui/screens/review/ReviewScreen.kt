package de.elvah.charge.features.adhoc_charging.ui.screens.review

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R
import de.elvah.charge.features.adhoc_charging.ui.screens.review.model.PaymentSummaryUI
import de.elvah.charge.platform.ui.components.BasicCard
import de.elvah.charge.platform.ui.components.CPOLogo
import de.elvah.charge.platform.ui.components.CopyLarge
import de.elvah.charge.platform.ui.components.CopyMedium
import de.elvah.charge.platform.ui.components.ElvahLogo
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.TitleMedium
import de.elvah.charge.platform.ui.components.TitleSmall
import de.elvah.charge.platform.ui.components.buttons.ButtonPrimary
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

@Composable
internal fun ReviewScreen(
    viewModel: ReviewViewModel,
    onDoneClick: () -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is ReviewState.Loading -> ReviewScreen_Loading()
        is ReviewState.Success -> ReviewScreen_Content(state, onDoneClick)
        is ReviewState.Error -> ReviewScreen_Error()
    }
}

@Composable
private fun ReviewScreen_Content(
    state: ReviewState.Success,
    onDoneClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.size(40.dp))
            CPOLogo(state.summary.cpoLogo)

            Spacer(Modifier.size(32.dp))
            Image(
                painter = painterResource(R.drawable.ic_success),
                null,
                modifier = Modifier.padding(32.dp)
            )
            Spacer(Modifier.size(32.dp))

            BasicCard(modifier = Modifier.fillMaxWidth()) {
                TitleSmall(state.summary.cpoName, fontWeight = FontWeight.W700)
                CopyMedium(state.summary.address)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CopyMedium(stringResource(R.string.code_label))
                    CopyMedium(state.summary.evseId)
                }
            }
            Spacer(modifier = Modifier.size(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {

                BasicCard(modifier = Modifier.weight(1f)) {
                    CopyMedium(stringResource(R.string.kw_charged_label))
                    TitleMedium(state.summary.consumedKWh.toString(), fontWeight = FontWeight.W700)
                }

                Spacer(modifier = Modifier.size(8.dp))

                BasicCard(modifier = Modifier.weight(1f)) {
                    CopyMedium(stringResource(R.string.charging_duration_label))
                    TitleMedium(state.summary.totalTime.toString(), fontWeight = FontWeight.W700)
                }
            }
            Spacer(modifier = Modifier.size(8.dp))

            BasicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CopyMedium(text = stringResource(R.string.total_cost_label))
                    CopyLarge(
                        text = state.summary.totalCost.toString() + " €",
                        fontWeight = FontWeight.W700
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            ButtonPrimary(
                stringResource(R.string.done_label),
                onClick = onDoneClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(16.dp))
            ElvahLogo()

        }
    }
}

@PreviewLightDark
@Composable
private fun ReviewScreen_Content_Preview() {
    ElvahChargeTheme {
        ReviewScreen_Content(
            ReviewState.Success(
                summary = PaymentSummaryUI(
                    evseId = "1",
                    cpoName = "Lidl Köpenicker Straße",
                    address = "",
                    totalTime = "0",
                    consumedKWh = 0.0,
                    cpoLogo = "",
                    totalCost = 0.0
                )
            ),
            onDoneClick = {}
        )
    }
}

@Composable
private fun ReviewScreen_Loading() {
    FullScreenLoading()
}

@Composable
private fun ReviewScreen_Error() {
    FullScreenError()
}
