package de.elvah.charge.platform.core.mvi

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

internal interface MVIBaseInterface<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.ViewEffect> {
    val state: StateFlow<State>
    val events: SharedFlow<Event>
    val effects: SharedFlow<Effect>
    val timeCapsule: TimeCapsule<State>

    fun event(transform: () -> Event)
    fun event(event: Event)
    fun sendEvent(event: Event, allowSideEffect: Boolean = false)
}

internal class MVIBaseDelegate<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.ViewEffect>(
    initialState: State,
    private val reducer: Reducer<State, Event, Effect>,
) : MVIBaseInterface<State, Event, Effect> {

    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    override val state: StateFlow<State>
        get() = _state.asStateFlow()

    private val _events: MutableSharedFlow<Event> = MutableSharedFlow()
    override val events: SharedFlow<Event>
        get() = _events.asSharedFlow()

    private val _effects: MutableSharedFlow<Effect> = MutableSharedFlow()
    override val effects: SharedFlow<Effect>
        get() = _effects.asSharedFlow()

    override val timeCapsule: TimeCapsule<State> = TimeTravelCapsule { storedState ->
        _state.tryEmit(storedState)
    }

    init {
        timeCapsule.addState(initialState)
    }

    private fun effect(effect: Effect) {
        _effects.tryEmit(effect)
    }

    override fun event(transform: () -> Event) {
        event(transform())
    }

    override fun event(event: Event) {
        _events.tryEmit(event)
    }

    override fun sendEvent(event: Event, allowSideEffect: Boolean) {
        val (newState, effect) = reducer.reduce(_state.value, event)

        val success = _state.tryEmit(newState)

        if (success) {
            timeCapsule.addState(newState)
        }

        if (allowSideEffect) {
            effect?.let {
                effect(it)
            }
        }
    }
}
