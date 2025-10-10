package de.elvah.charge.platform.core.mvi

internal abstract class MVIBase<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.ViewEffect>(
    initialState: State,
    reducer: Reducer<State, Event, Effect>,
) : MVIBaseInterface<State, Event, Effect> by MVIBaseDelegate(initialState, reducer)
