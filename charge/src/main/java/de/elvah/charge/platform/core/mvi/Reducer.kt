package de.elvah.charge.platform.core.mvi

internal fun interface Reducer<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.ViewEffect> {
    interface ViewState

    interface ViewEvent

    interface ViewEffect

    fun reduce(previousState: State, event: Event): Result<State, Effect>

    data class Result<State : ViewState, Effect : ViewEffect>(
        val newState: State,
        val effect: Effect? = null,
    )
}