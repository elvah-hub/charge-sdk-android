package de.elvah.charge.platform.core.mvi

import androidx.lifecycle.ViewModel

internal abstract class MVIBaseViewModel<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.ViewEffect>(
    initialState: State,
    reducer: Reducer<State, Event, Effect>,
) : ViewModel(), MVIBaseInterface<State, Event, Effect> by MVIBaseDelegate(initialState, reducer)
