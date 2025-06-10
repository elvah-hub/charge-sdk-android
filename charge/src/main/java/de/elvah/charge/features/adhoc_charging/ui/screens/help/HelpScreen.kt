package de.elvah.charge.features.adhoc_charging.ui.screens.help

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.R
import de.elvah.charge.features.payments.domain.model.OrganisationDetails
import de.elvah.charge.features.payments.domain.model.SupportContacts
import de.elvah.charge.platform.ui.components.CPOLogo
import de.elvah.charge.platform.ui.components.CopyMedium
import de.elvah.charge.platform.ui.components.ElvahLogo
import de.elvah.charge.platform.ui.components.FullScreenError
import de.elvah.charge.platform.ui.components.FullScreenLoading
import de.elvah.charge.platform.ui.components.SecondaryButton
import de.elvah.charge.platform.ui.components.TitleSmall
import de.elvah.charge.platform.ui.components.TopAppBar


@Composable
internal fun HelpAndSupportScreen(
    viewModel: HelpViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is HelpState.Loading -> HelpScreen_Loading()
        is HelpState.Success -> HelpScreen_Content(state, onBackClick)
        is HelpState.Error -> HelpScreen_Error()
    }
}

@Composable
private fun HelpScreen_Content(
    state: HelpState.Success,
    onBackClick: () -> Unit,
) {
    Scaffold(topBar = {
        TopAppBar(
            stringResource(R.string.help_support_heading),
            onBackClick
        )
    }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(16.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CPOLogo(
                url = state.organisationDetails.logoUrl,
                modifier = Modifier.padding(40.dp)
            )

            Image(
                painter = painterResource(R.drawable.ic_support),
                contentDescription = null
            )

            Spacer(modifier = Modifier.size(24.dp))

            TitleSmall(
                stringResource(R.string.support_title),
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(horizontal = 70.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            CopyMedium(
                stringResource(R.string.support_subtitle),
                modifier = Modifier.padding(horizontal = 30.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(40.dp))

            SupportActions(state.organisationDetails.supportContacts)

            Spacer(Modifier.weight(1f))

            ElvahLogo()
        }
    }

}

@Preview
@Composable
private fun HelpScreen_Content_Preview() {
    HelpScreen_Content(
        HelpState.Success(
            organisationDetails = OrganisationDetails(
                "",
                "",
                "",
                "",
                supportContacts = SupportContacts()
            )
        )
    ) {}
}

@Composable
private fun SupportActions(supportContacts: SupportContacts, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        supportContacts.phone?.let {
            SecondaryButton(
                supportContacts.phone.toString(),
                icon = R.drawable.ic_phone,
                modifier = Modifier.fillMaxWidth()
            ) { context.openDialer(it) }
        }


        if (supportContacts.agent != null) {
            SecondaryButton(
                text = stringResource(R.string.contact_support_agent),
                icon = R.drawable.ic_support_agent, modifier = Modifier.fillMaxWidth()
            ) { }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            supportContacts.email?.let {
                SecondaryButton(
                    stringResource(R.string.email),
                    icon = R.drawable.ic_email,
                    modifier = Modifier.weight(1f)
                ) { context.openEmail(it) }
            }

            supportContacts.whatsapp?.let {
                SecondaryButton(
                    stringResource(R.string.whatsapp),
                    icon = R.drawable.ic_whatsapp,
                    modifier = Modifier.weight(1f)
                ) {
                    context.openWhatsapp(it)
                }
            }
        }
    }
}

private fun Context.openDialer(phoneNumber: String) {
    val intent = Intent(
        Intent.ACTION_DIAL,
        Uri.fromParts("tel", phoneNumber, null)
    )
    startActivity(intent)
}

private fun Context.openEmail(email: String) {
    val intent = Intent(
        Intent.ACTION_SENDTO,
        Uri.fromParts("mailto", email, null)
    )
    startActivity(intent)
}

private fun Context.openWhatsapp(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = "https://api.whatsapp.com/send?phone=$phoneNumber".toUri()
    }
    startActivity(intent)
}

@Composable
private fun HelpScreen_Loading() {
    FullScreenLoading()
}

@Composable
private fun HelpScreen_Error() {
    FullScreenError()
}

