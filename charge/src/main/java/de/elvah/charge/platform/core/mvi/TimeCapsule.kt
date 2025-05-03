package de.elvah.charge.platform.core.mvi

internal interface TimeCapsule<S : Reducer.ViewState> {
    fun addState(state: S)
    fun selectState(position: Int)
    fun getStates(): List<S>
}

internal class TimeTravelCapsule<S : Reducer.ViewState>(
    private val onStateSelected: (S) -> Unit,
) : TimeCapsule<S> {

    private val states = mutableListOf<S>()

    override fun addState(state: S) {
        states.add(state)
    }

    override fun selectState(position: Int) {
        onStateSelected(states[position])
    }

    override fun getStates(): List<S> {
        return states
    }
}