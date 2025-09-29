package de.elvah.charge.platform.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.R
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended

@Composable
internal fun BrandBadge(
    @DrawableRes brandIcon: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(brandIcon),
        contentDescription = contentDescription,
        modifier = modifier
            .clip(CircleShape)
            .background(Color.White)
            .padding(4.dp)
    )
}

@Preview
@Composable
private fun BrandBadge_Preview() {
    BrandBadge(android.R.drawable.star_on, "Brand")
}


@Composable
internal fun ChargingBadge(
    progress: Int,
    modifier: Modifier = Modifier,
    tint: Color = MaterialTheme.colorSchemeExtended.brand,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_battery),
            contentDescription = null,
            tint = tint

        )

        Text(
            stringResource(R.string.progress_percentage_template, progress),
            color = tint,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Preview
@Composable
private fun ChargingBadge_Preview() {
    ChargingBadge(23)
}
