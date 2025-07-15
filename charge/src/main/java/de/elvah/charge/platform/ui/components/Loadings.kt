package de.elvah.charge.platform.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import de.elvah.charge.platform.ui.theme.ElvahChargeTheme

@Composable
fun FullScreenLoading(modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }
}

@PreviewLightDark
@Composable
private fun FullScreenLoading_Preview() {
    ElvahChargeTheme {
        FullScreenLoading()
    }
}
