package de.elvah.charge.platform.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.elvah.charge.platform.ui.theme.brand
import de.elvah.charge.platform.ui.theme.copyLargeBold
import de.elvah.charge.platform.ui.theme.onBrand
import kotlinx.coroutines.delay
import kotlin.math.roundToInt


private val swipingBoxSize = 48.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeButton(
    text: String,
    onSwipeText: String? = null,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.brand,
    contentColor: Color = MaterialTheme.colorScheme.onBrand,
    shouldMoveText: Boolean = true,
    onSwiped: () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        contentColor = Color.White,
        shape = MaterialTheme.shapes.small
    ) {
        var swipeCompleted by remember { mutableStateOf(false) }
        var containerWidth by remember { mutableIntStateOf(Int.MAX_VALUE) }

        Box(
            modifier = Modifier
                .onGloballyPositioned {
                    containerWidth = it.size.width
                },
            contentAlignment = Alignment.Center
        ) {
            val icon = if (swipeCompleted) {
                Icons.Outlined.Done
            } else {
                Icons.AutoMirrored.Outlined.KeyboardArrowRight
            }

            val swipingBoxPx = with(LocalDensity.current) { swipingBoxSize.toPx() }
            val maxSwipeDistance = containerWidth - swipingBoxPx

            LaunchedEffect(swipeCompleted) {
                if (swipeCompleted) {
                    //maybe add vibration here
                    delay(1000)
                    onSwiped()
                }
            }
            val flingSpec = rememberSplineBasedDecay<Float>()

            val state = remember {
                AnchoredDraggableState(
                    initialValue = DragAnchors.Start,
                    positionalThreshold = { distance: Float -> distance * 0.99f },
                    snapAnimationSpec = tween<Float>(),
                    velocityThreshold = { Float.MAX_VALUE },
                    decayAnimationSpec = flingSpec
                ).apply {
                    updateAnchors(
                        DraggableAnchors {
                            DragAnchors.Start at 0f
                            DragAnchors.End at maxSwipeDistance
                        }
                    )
                }
            }

            LaunchedEffect(maxSwipeDistance) {
                state.updateAnchors(
                    DraggableAnchors {
                        DragAnchors.Start at 0f
                        DragAnchors.End at maxSwipeDistance
                    }
                )
            }

            val dragAnchorOffset = IntOffset(
                x = state.requireOffset().roundToInt(),
                y = 0
            )

            if (state.requireOffset() >= maxSwipeDistance) {
                swipeCompleted = true
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset { dragAnchorOffset }
                    .anchoredDraggable(
                        state = state,
                        enabled = !swipeCompleted,
                        orientation = Orientation.Horizontal
                    )
                    .size(swipingBoxSize),
            ) {

                Crossfade(
                    modifier = Modifier.align(Alignment.Center),
                    label = "animate icon transition",
                    targetState = icon
                ) { targetState ->
                    SwipeIndicator(
                        icon = targetState,
                        backgroundColor = contentColor,
                        tint = backgroundColor
                    )
                }
            }

            if (!swipeCompleted) {
                Text(
                    modifier = if (shouldMoveText) {
                        Modifier.offset { dragAnchorOffset }
                    } else {
                        Modifier
                    },
                    text = text,
                    style = copyLargeBold,
                    color = contentColor
                )
            }
            if (onSwipeText != null) {
                AnimatedVisibility(swipeCompleted) {
                    Text(
                        text = onSwipeText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.W400,
                    )
                }
            }
        }
    }
}

enum internal class DragAnchors {
    Start,
    End,
}

@Preview
@Composable
private fun SwipeButtonPreview() {
    SwipeButton(
        text = "Swipe",
        onSwiped = {},
    )
}

@Composable
private fun SwipeIndicator(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
    backgroundColor: Color,
    tint: Color,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(backgroundColor)
            .padding(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
        )
    }
}

@Preview
@Composable
private fun SwipeIndicator_Preview() {
    SwipeIndicator(backgroundColor = Color.White, tint = MaterialTheme.colorScheme.brand)

}