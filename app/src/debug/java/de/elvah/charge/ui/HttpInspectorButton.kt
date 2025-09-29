package de.elvah.charge.ui

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.elvah.charge.development.DevelopmentTools

@Composable
internal fun HttpInspectorButton(context: android.content.Context) {
    Button({
        context.startActivity(DevelopmentTools.getHttpInspectorIntent())
    }) {
        Text("Open HTTP Inspector")
    }
}
