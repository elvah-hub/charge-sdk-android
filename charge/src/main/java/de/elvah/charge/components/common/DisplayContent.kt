package de.elvah.charge.components.common

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import de.elvah.charge.public_api.DisplayBehavior

@Composable
internal fun DisplayContent(
    displayBehaviour: DisplayBehavior,
    content: @Composable () -> Unit
) {
    if (displayBehaviour != DisplayBehavior.WHEN_CONTENT_AVAILABLE) {
        content()
    } else {
        // workaround for lazy lists where the graph is not instantiated after content is loaded
        Spacer(Modifier.height(1.dp))
    }
}
