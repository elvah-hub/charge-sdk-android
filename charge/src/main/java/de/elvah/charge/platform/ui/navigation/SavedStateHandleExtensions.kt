package de.elvah.charge.platform.ui.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


internal inline fun <reified T> SavedStateHandle.asFlow(): Flow<T> {
    val routeArgs: T = toRoute()
    return flow { emit(routeArgs) }
}
