package de.elvah.charge.platform.ui.components.site

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.platform.core.android.openMap
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.copyMedium
import de.elvah.charge.platform.ui.theme.titleSmallBold

@Composable
internal fun SiteDetailHeader(
    operatorName: String,
    address: String?,
    coordinates: Pair<Double, Double>,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Text(
            text = operatorName,
            color = MaterialTheme.colorScheme.primary,
            style = titleSmallBold
        )

        address?.let {
            Row(
                modifier = Modifier
                    .padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .clickable {
                            context.openMap(
                                lat = coordinates.first,
                                lng = coordinates.second,
                                title = operatorName,
                            )
                        },
                    text = it,
                    color = MaterialTheme.colorScheme.secondary,
                    style = copyMedium,
                    textDecoration = TextDecoration.Underline,
                )

                Spacer(Modifier.width(4.dp))

                Icon(
                    modifier = Modifier
                        .size(16.dp),
                    painter = painterResource(R.drawable.ic_open_external),
                    tint = MaterialTheme.colorScheme.secondary,
                    contentDescription = null,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun SiteDetailHeader_Preview() {
    ElvahChargeTheme {
        SiteDetailHeader(
            operatorName = "Lidl Köpenicker Straße",
            address = "Köpenicker Straße 145 12683 Berlin",
            coordinates = Pair(0.0, 0.0)
        )
    }
}
