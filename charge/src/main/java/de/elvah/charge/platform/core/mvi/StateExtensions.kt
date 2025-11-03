package de.elvah.charge.platform.core.mvi

internal inline fun <reified T : Reducer.ViewState> Reducer.ViewState.ifState(
    block: (T) -> Reducer.ViewState
): Reducer.ViewState {
    return if (this is T) {
        block(this)
    } else {
        this
    }
}
