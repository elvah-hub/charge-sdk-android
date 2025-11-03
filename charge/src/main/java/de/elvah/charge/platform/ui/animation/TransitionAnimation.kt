package de.elvah.charge.platform.ui.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.navigation.NavBackStackEntry

private const val TRANSITION_DURATION_MILLIS: Int = 400

internal fun slideFromBottom(durationMillis: Int = TRANSITION_DURATION_MILLIS): AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards EnterTransition? =
    {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Up,
            tween(durationMillis)
        )
    }

internal fun slideToBottom(durationMillis: Int = TRANSITION_DURATION_MILLIS): AnimatedContentTransitionScope<NavBackStackEntry>.() -> @JvmSuppressWildcards ExitTransition? =
    {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.Down,
            tween(durationMillis)
        )
    }
