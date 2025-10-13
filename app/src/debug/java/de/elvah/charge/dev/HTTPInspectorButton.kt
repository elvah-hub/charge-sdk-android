package de.elvah.charge.dev

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun HTTPInspectorButton() {
    Button({
        // todo:
    }) {
        Text("Open HTTP inspector")
    }
}

@Preview
@Composable
private fun HTTPInspectorButtonPreview() {
    HTTPInspectorButton()
}
