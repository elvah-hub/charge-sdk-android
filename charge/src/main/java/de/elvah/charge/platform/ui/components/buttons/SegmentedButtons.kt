package de.elvah.charge.platform.ui.components.buttons

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun DaySelector(
    selectedDay: Int,
    modifier: Modifier = Modifier,
    selectedIcon: @Composable () ->
    Unit = {},
    onDaySelected: (Int) -> Unit
) {
    val options = listOf("Yesterday", "Today", "Tomorrow")

    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = MaterialTheme.colorScheme.secondary
                ),
                onClick = {
                    onDaySelected(index)
                },
                icon = selectedIcon,
                selected = index == selectedDay,
                label = { Text(label) }
            )
        }
    }
}
