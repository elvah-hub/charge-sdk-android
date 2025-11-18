package de.elvah.charge.platform.core.mvi

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

internal abstract class MVIBaseViewModel<State : Reducer.ViewState, Event : Reducer.ViewEvent, Effect : Reducer.ViewEffect>(
    initialState: State,
    private val reducer: Reducer<State, Event, Effect>,
) : ViewModel() {
    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State>
        get() = _state.asStateFlow()

    private val _events: MutableSharedFlow<Event> = MutableSharedFlow()
    val events: SharedFlow<Event>
        get() = _events.asSharedFlow()

    private val _effects: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effects: SharedFlow<Effect>
        get() = _effects.asSharedFlow()


    val timeCapsule: TimeCapsule<State> = TimeTravelCapsule { storedState ->
        _state.tryEmit(storedState)
    }

    init {
        timeCapsule.addState(initialState)
    }

    private suspend fun effect(effect: Effect) {
        Log.d("Effect", effect.toString())
        _effects.emit(effect)
    }

    fun event(transform: () -> Event) {
        event(transform())
    }

    fun event(event: Event) {
        _events.tryEmit(event)
    }

    suspend fun sendEvent(event: Event, allowSideEffect: Boolean = true) {
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


