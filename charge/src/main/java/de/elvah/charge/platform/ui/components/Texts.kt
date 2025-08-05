package de.elvah.charge.platform.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import de.elvah.charge.platform.ui.theme.copyLarge
import de.elvah.charge.platform.ui.theme.copyMedium
import de.elvah.charge.platform.ui.theme.copySmall
import de.elvah.charge.platform.ui.theme.copyXLarge
import de.elvah.charge.platform.ui.theme.titleLarge
import de.elvah.charge.platform.ui.theme.titleMedium
import de.elvah.charge.platform.ui.theme.titleSmall
import de.elvah.charge.platform.ui.theme.titleXLarge

@Composable
fun TitleXLarge(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.W400,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text,
        modifier = modifier,
        style = titleXLarge,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color
    )
}

@Composable
fun TitleLarge(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.W400,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text,
        modifier = modifier,
        style = titleLarge,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color
    )
}

@Composable
fun TitleMedium(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.W400,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text,
        modifier = modifier,
        style = titleMedium,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color
    )
}

@Composable
fun TitleSmall(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.W400,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text,
        modifier = modifier,
        style = titleSmall,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color
    )
}

@Composable
fun CopyXLarge(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.W400,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text,
        modifier = modifier,
        style = copyXLarge,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color
    )
}

@Composable
fun CopyLarge(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.W400,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.primary
) {
    Text(
        text,
        modifier = modifier,
        style = copyLarge,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color
    )
}

@Composable
fun CopyMedium(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.W400,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.primary,
    textDecoration: TextDecoration = TextDecoration.None
) {
    Text(
        text,
        modifier = modifier,
        style = copyMedium,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color,
        textDecoration = textDecoration
    )
}

@Composable
fun CopySmall(
    text: String,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight = FontWeight.W400,
    textAlign: TextAlign = TextAlign.Start,
    color: Color = MaterialTheme.colorScheme.secondary
) {
    Text(
        text,
        modifier = modifier,
        style = copySmall,
        fontWeight = fontWeight,
        textAlign = textAlign,
        color = color
    )
}
