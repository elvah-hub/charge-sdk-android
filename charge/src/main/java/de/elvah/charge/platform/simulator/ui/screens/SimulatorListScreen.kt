package de.elvah.charge.platform.simulator.ui.screens

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
