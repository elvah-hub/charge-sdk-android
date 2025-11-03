package de.elvah.charge.features.adhoc_charging.ui.screens.sitedetail.chargepointslist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.R
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme
import de.elvah.charge.platform.ui.theme.colors.ElvahChargeThemeExtension.colorSchemeExtended
import de.elvah.charge.platform.ui.theme.copyLarge

@Composable
internal fun SearchChargePointInputField(
    modifier: Modifier = Modifier,
    searchInput: String,
    onSearchInputChange: (String) -> Unit,
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(
            space = 12.dp,
        ),
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .padding(
                    // spacing here until the input field base component remove the extra top padding
                    bottom = 8.dp,
                )
                .fillMaxWidth(),
            value = searchInput,
            onValueChange = {
                onSearchInputChange(it)
            },
            singleLine = true,
            shape = RoundedCornerShape(
                size = 8.dp,
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorSchemeExtended.decorativeStroke,
                unfocusedBorderColor = MaterialTheme.colorSchemeExtended.decorativeStroke.copy(
                    alpha = 0.16f,
                ),
            ),
            label = {
                Text(
                    text = stringResource(R.string.charge_points_search_input_hint),
                    style = copyLarge.copy(
                        color = MaterialTheme.colorScheme.secondary,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
            },
            leadingIcon = {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(id = R.drawable.ic_search),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
            },
            trailingIcon = searchInput
                .takeIf { it.isNotEmpty() }
                ?.let {
                    {
                        IconButton(
                            onClick = { onSearchInputChange("") },
                            content = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_close_filled),
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        )
                    }
                },
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Text,
            ),
            keyboardActions = KeyboardActions(
                onAny = { focusManager.clearFocus() },
            ),
        )
    }
}

@PreviewLightDark
@Composable
private fun SearchChargePointInputFieldPreview() {
    ElvahChargeTheme {
        SearchChargePointInputField(
            searchInput = "123",
            onSearchInputChange = {},
        )
    }
}
