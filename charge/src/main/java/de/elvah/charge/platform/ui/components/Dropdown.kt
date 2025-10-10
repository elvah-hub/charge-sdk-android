package de.elvah.charge.platform.ui.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun DropdownMenuButton(selectedOption:String, onClick: () -> Unit){
    TextButton(
        onClick = onClick
    ) {
        DropdownLabel(selectedOption)
    }
}


@Composable
internal fun DropdownLabel(selectedText: String) {
    CopySmall(selectedText)
    Spacer(modifier = Modifier.width(6.dp))
    Icon(
        imageVector = Icons.Filled.ArrowDropDown,
        tint = MaterialTheme.colorScheme.primary,
        contentDescription = "Dropdown Arrow"
    )
}
