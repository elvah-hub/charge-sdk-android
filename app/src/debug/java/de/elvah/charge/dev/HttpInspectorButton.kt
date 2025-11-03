package de.elvah.charge.dev

import android.content.Context
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.chuckerteam.chucker.api.Chucker

@Composable
fun HttpInspectorButton(context: Context) {
    HttpInspectorButton(
        onClick = {
            context.startActivity(Chucker.getLaunchIntent(context))
        },
    )
}

@Composable
private fun HttpInspectorButton(onClick: () -> Unit) {
    Button(onClick) {
        Text("Open HTTP inspector")
    }
}

@Preview
@Composable
private fun HTTPInspectorButtonPreview() {
    HttpInspectorButton(
        onClick = {},
    )
}
