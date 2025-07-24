package de.elvah.charge.platform.simulator.ui.screens

import android.widget.ToggleButton
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.elvah.charge.platform.simulator.data.BooleanSimulatorValue
import de.elvah.charge.platform.simulator.data.ConfigType
import de.elvah.charge.platform.simulator.data.SimulatorConfig
import de.elvah.charge.platform.simulator.data.SimulatorConfigValue
import de.elvah.charge.platform.simulator.ui.SimulatorListViewModel
import de.elvah.charge.platform.ui.components.Chevron
import de.elvah.charge.platform.ui.components.TopAppBar
/*
@Composable
fun SimulatorListScreen(
    simulatorListViewModel: SimulatorListViewModel,
    modifier: Modifier = Modifier,
    onConfigClick: (SimulatorConfig<*>) -> Unit,
    onBackClicked: () -> Unit
) {

    val state by simulatorListViewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar("Simulator", onBackClicked)
        }
    ) {
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(it),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.size) { index ->
                ConfigItem(
                    configItem = state[index],
                    modifier = modifier,
                    onConfigClick = onConfigClick,
                    onValueChange = { value ->
                        simulatorListViewModel.configItemLists.updateConfig(
                            id = state[index].id,
                            onValueChange = {
                                it.copy(value = value)
                            }
                        )
                    }
                )
            }
        }
    }
}


@Composable
private fun ConfigItem(
    configItem: SimulatorConfig<*>,
    modifier: Modifier = Modifier,
    onConfigClick: (SimulatorConfig<*>) -> Unit,
    onValueChange: (SimulatorConfigValue<Boolean>) -> Unit
) {

    when (configItem.type) {
        ConfigType.TEXT -> DefaultConfig(configItem)
        ConfigType.SINGLE_SELECTION -> {

        }

        ConfigType.MULTIPLE_SELECTION -> {

        }

        ConfigType.TOGGLE -> ToggleConfig(configItem, onValueChange = onValueChange)
    }
}

@Composable
private fun DefaultConfig(
    configItem: SimulatorConfig<*>,
    modifier: Modifier = Modifier,
    onConfigClick: (SimulatorConfig<*>) -> Unit = {}
) {

    Row(
        modifier = modifier
            .clickable { onConfigClick(configItem) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(configItem.title, modifier = Modifier.weight(1f))

        Chevron()
    }
}

@Composable
private fun ToggleConfig(
    configItem: SimulatorConfig<*>,
    modifier: Modifier = Modifier,
    onValueChange: (SimulatorConfigValue<Boolean>) -> Unit
) {

    val value = configItem.value as BooleanSimulatorValue

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(configItem.title, modifier = Modifier.weight(1f))

        Switch(
            checked = value.value,
            onCheckedChange = {
                onValueChange(BooleanSimulatorValue(it))
            }
        )
    }
}

data class ConfigItem(
    val id: String,
    val name: String,
)


 */
