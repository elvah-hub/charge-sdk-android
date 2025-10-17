package de.elvah.charge.platform.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme


@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopAppBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
) {
    TopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = onBackClick?.let {
            { BackIcon(onClick = onBackClick) }
        } ?: {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DismissableTopAppBar(
    title: String,
    onDismissClick: (() -> Unit)? = null,
    menuItems: List<MenuItem> = emptyList(),
) {
    val actions = menuItems.groupBy { it.inMenu }

    CenterAlignedTopAppBar(
        title = {
            Text(title)
        },
        navigationIcon = onDismissClick?.let {
            {
                MinifyIcon(onClick = onDismissClick)
            }
        } ?: {},
        actions = {
            actions[false].orEmpty().map {
                IconButton(
                    onClick = it.onClick,
                    modifier = Modifier.background(
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
                ) {
                    it.leadingIcon()
                }
            }

            if (actions[true].orEmpty().isNotEmpty()) {
                DropdownMenuWithDetails(actions[true].orEmpty())
            }
        }
    )
}

@Composable
private fun DropdownMenuWithDetails(menuItems: List<MenuItem>) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(16.dp)
    ) {
        IconButton(
            onClick = { expanded = !expanded },
            modifier = Modifier.background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            )
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.MoreVert,
                contentDescription = if (expanded) "Close" else "More",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            menuItems.forEach { menuItem ->
                DropdownMenuItem(
                    text = { Text(menuItem.text, color = menuItem.tint) },
                    leadingIcon = menuItem.leadingIcon,
                    onClick = menuItem.onClick
                )
            }
        }
    }
}

internal data class MenuItem(
    val text: String,
    val leadingIcon: @Composable () -> Unit,
    val onClick: () -> Unit,
    val tint: Color,
    val inMenu: Boolean = true
)


@Composable
internal fun BackIcon(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
    }
}

@Composable
internal fun MinifyIcon(onClick: () -> Unit) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.padding(start = 16.dp).background(MaterialTheme.colorScheme.background, shape = CircleShape)
    ) {
        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Back")
    }
}

@Preview
@Composable
private fun DefaultTopAppBar_Preview() {
    TopAppBar(title = "TopBar title")
}

@Preview
@Composable
private fun TopAppBarWithBack_Preview() {
    TopAppBar(title = "TopBar title", {})
}

@PreviewLightDark
@Composable
private fun DismissableTopAppBar_Preview() {
    ElvahChargeTheme {
        DismissableTopAppBar(title = "TopBar title", onDismissClick = {})
    }
}

@PreviewLightDark
@Composable
private fun DismissableTopAppBarWithActions_Preview() {
    ElvahChargeTheme {
        DismissableTopAppBar(
            title = "TopBar title", onDismissClick = {}, menuItems = listOf(
                MenuItem(
                    text = "Action 1",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {},
                    tint = MaterialTheme.colorScheme.primary,
                    inMenu = true
                ),

                MenuItem(
                    text = "Action 2",
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    onClick = {},
                    tint = MaterialTheme.colorScheme.primary,
                    inMenu = false
                )
            )
        )
    }
}



