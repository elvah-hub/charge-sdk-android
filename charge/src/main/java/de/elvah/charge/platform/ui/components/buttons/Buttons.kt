package de.elvah.charge.platform.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.R
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copyLargeBold

@Composable
internal fun GooglePayButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Text(stringResource(R.string.google_pay_button))
    }
}

@PreviewLightDark
@Composable
private fun GooglePayButton_Preview() {
    ElvahChargeTheme {
        GooglePayButton({})
    }
}

@Composable
internal fun ButtonPrimary(
    text: String, modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    onClick: () -> Unit,
) {

    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorSchemeExtended.brand,
            contentColor = MaterialTheme.colorSchemeExtended.onBrand,
        )
    ) {
        icon?.let {
            Icon(painter = painterResource(it), contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
        }
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 8.dp),
            style = copyLargeBold,
            color = MaterialTheme.colorSchemeExtended.onBrand,
        )
    }
}

@PreviewLightDark
@Composable
private fun PrimaryButton_Preview() {
    ElvahChargeTheme {
        ButtonPrimary("Hello") {}

    }
}


@Composable
internal fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    tint: Color = MaterialTheme.colorSchemeExtended.brand,
    onClick: () -> Unit,
) {

    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = tint,
        ),
        border = BorderStroke(1.dp, tint)
    ) {
        icon?.let {
            Icon(painter = painterResource(it), contentDescription = null)
            Spacer(modifier = Modifier.size(8.dp))
        }
        Text(text)
    }
}

@PreviewLightDark
@Composable
private fun SecondaryButton_Preview() {
    ElvahChargeTheme {
        SecondaryButton("Hello") {}
    }
}

@PreviewLightDark
@Composable
private fun SecondaryButton_WithIcon_Preview() {
    ElvahChargeTheme {
        SecondaryButton("Email", icon = R.drawable.ic_email) {}
    }
}


@Composable
internal fun ButtonTertiary(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    TextButton(onClick, modifier) {
        val color = MaterialTheme.colorScheme.primary
        Text(
            text,
            modifier = Modifier.drawBehind {
                val strokeWidthPx = 1.dp.toPx()
                val verticalOffset = size.height - 2.sp.toPx()
                drawLine(
                    color = color,
                    strokeWidth = strokeWidthPx,
                    start = Offset(0f, verticalOffset),
                    end = Offset(size.width, verticalOffset)
                )
            },
            color = color,
        )
    }
}

@PreviewLightDark
@Composable
private fun ButtonTertiary_Preview() {
    ElvahChargeTheme {
        ButtonTertiary("Hello") {}
    }
}
