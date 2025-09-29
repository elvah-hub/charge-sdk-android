package de.elvah.charge.platform.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

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

@Composable
internal fun BackIcon(onClick: () -> Unit) {
    IconButton(onClick) {
        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
