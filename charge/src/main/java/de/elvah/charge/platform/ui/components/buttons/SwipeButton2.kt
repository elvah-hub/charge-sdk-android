package de.elvah.charge.platform.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 *
 * This composable creates a custom, swipeable button that resembles a "slide to book" interaction.
 * It is composed of two main parts:
 *  - The outer track (the full-width button background) which displays the label.
 *  - The inner slider thumb, which can be dragged from left to right.
 *
 *
 * @param btnText Text to display on the outer button track (e.g., "Book Ride ₹199")
 * @param outerBtnBackgroundColor Background color for the full-width outer button
 * @param sliderBtnBackgroundColor Background color for the draggable thumb button
 * @param sliderBtnIcon Icon shown inside the slider thumb (e.g., car or arrow icon)
 * @param onBtnSwipe Callback triggered once the user slides to complete the booking
 */
@Composable
internal fun SlideToBookButton(
    btnText: String,
    outerBtnBackgroundColor: Color,
    sliderBtnBackgroundColor: Color,
    @DrawableRes sliderBtnIcon: Int,
    sliderPositionPx: Float,
    onDragUpdated: (Float) -> Unit,
    onBtnSwipe: () -> Unit,
) {
    // Slider button width
    val sliderButtonWidthDp = 70.dp

    /**
     * ----------------------------------------
     * ✨ Step 1:
     * • Convert slider button width into pixels so we can use it in math
     * • Define a variable to compute the current horizontal position of the slider button (in pixels)
     * • Define a variable to capture the total width of the outer button in pixels
     * ----------------------------------------
     */
    val density = LocalDensity.current
    val sliderButtonWidthPx = with(density) { sliderButtonWidthDp.toPx() }
    var boxWidthPx by remember { mutableIntStateOf(0) }

    /**
     * ----------------------------------------
     * ✨ Step 12: Add a flag that tells us when we need to show this loading indicator
     * ----------------------------------------
     */
    var showLoadingIndicator by remember { mutableStateOf(false) }

    /**
     * ----------------------------------------
     * ✨ Step 5: Calculate drag progress percentage (0f to 1f)
     * ----------------------------------------
     */
    val dragProgress = remember(sliderPositionPx, boxWidthPx) {
        if (boxWidthPx > 0) {
            (sliderPositionPx / (boxWidthPx - sliderButtonWidthPx)).coerceIn(0f, 1f)
        } else {
            0f
        }
    }

    /**
     * ----------------------------------------
     * ✨ Step 6: Alpha value for the button label — 1 when untouched, fades to 0 as drag progresses
     * ----------------------------------------
     */
    val textAlpha = 1f - dragProgress

    /**
     * ----------------------------------------
     * ✨ Step 8:
     * • Add a flag to indicate the slide is complete.
     * • Animate the shrinking of the outer button i.e. scale the outer button.
     * • Animate the fading of the slider button i.e. make it disappear.
     * ----------------------------------------
     */
    var sliderComplete by remember { mutableStateOf(false) }
    val trackScale by animateFloatAsState(
        targetValue = if (sliderComplete) 0f else 1f,
        animationSpec = tween(durationMillis = 300), label = "trackScale"
    )
    val sliderAlpha by animateFloatAsState(
        targetValue = if (sliderComplete) 0f else 1f,
        animationSpec = tween(durationMillis = 300), label = "sliderAlpha"
    )

    /**
     * ----------------------------------------
     * ✨ Step 9: Mark slide as complete once drag passes 80%.
     * This calls the onBtnSwipe method so users can perform whatever action is needed for their app.
     * ----------------------------------------
     */
    LaunchedEffect(dragProgress) {
        if (dragProgress >= 0.8f && !sliderComplete) {
            sliderComplete = true
            showLoadingIndicator = true // ✨ Step 13: Update flag when slider is completed
            onBtnSwipe()
        }
    }

    // The root layout for the button — stretches full width and has fixed height
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(55.dp)
            /**
             * ----------------------------------------
             * ✨ Step 2: Capture the full width of the button once it's laid out
             * ----------------------------------------
             */
            .onSizeChanged { size ->
                boxWidthPx = size.width
            }
    ) {
        // Outer track — acts as the base of the button
        Box(
            modifier = Modifier
                .matchParentSize()
                /**
                 * ----------------------------------------
                 * ✨ Step 10: Animate scaling/shrinking of the button
                 * ----------------------------------------
                 */
                .graphicsLayer(scaleX = trackScale, scaleY = 1f)
                .background(
                    color = outerBtnBackgroundColor,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            // The center-aligned button label
            Text(
                text = btnText,
                modifier = Modifier
                    .align(Alignment.Center)
                    /**
                     * ----------------------------------------
                     * ✨ Step 7: Apply the dynamic transparency to the label
                     * ----------------------------------------
                     */
                    .alpha(textAlpha)
            )
        }

        // Slider thumb container, positioned at the left edge of the button
        Row(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(1.dp)
                /**
                 * ----------------------------------------
                 * ✨ Step 3: Shift the slider button based on drag position (px to dp conversion)
                 * ----------------------------------------
                 */
                .offset(x = with(density) { sliderPositionPx.toDp() })
                /**
                 * ----------------------------------------
                 * ✨ Step 4: Handle horizontal drag gestures
                 * ----------------------------------------
                 */
                .draggable(
                    orientation = Orientation.Horizontal,
                    state = rememberDraggableState { delta ->
                        // Calculate new potential position
                        val newPosition = sliderPositionPx + delta
                        // Clamp it within 0 to (totalWidth - slider button width)
                        val maxPosition = boxWidthPx - sliderButtonWidthPx
                        onDragUpdated(
                            newPosition.coerceIn(0f, maxPosition)
                        )
                    },
                    onDragStarted = { /* Optional: add feedback or animation here */ },
                    onDragStopped = {
                        // TODO: In next step, we’ll trigger onBtnSwipe if drag passes threshold
                    }
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    /**
                     * ----------------------------------------
                     * ✨ Step 11: Animate fade out of the slider button
                     * ----------------------------------------
                     */
                    .alpha(sliderAlpha)
                    .graphicsLayer { alpha = sliderAlpha }
            ) {
                // The draggable thumb itself
                SliderButton(
                    sliderBtnWidth = sliderButtonWidthDp,
                    sliderBtnBackgroundColor = sliderBtnBackgroundColor,
                    sliderBtnIcon = sliderBtnIcon
                )
            }
        }

        /**
         * ----------------------------------------
         * ✨ Step 14: Show the loading indicator after the slider reaches the end and the animation completes
         * ----------------------------------------
         */
        if (showLoadingIndicator) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Black
            )
        }
    }
}

/**
 *
 * This composable defines the visual appearance of the slider thumb — a small rounded box
 * that contains an icon (usually a car or arrow). It is positioned inside the larger
 * SlideToBookButton and will later be made draggable.
 *
 * @param sliderBtnBackgroundColor Background color for the thumb (distinct from the track)
 * @param sliderBtnIcon Icon displayed at the center of the thumb button
 */
@Composable
private fun SliderButton(
    sliderBtnWidth: Dp, // Width of the button
    sliderBtnBackgroundColor: Color, // Background color for the thumb
    @DrawableRes sliderBtnIcon: Int  // Icon shown inside the thumb
) {
    // Root Box for the slider thumb
    Box(
        modifier = Modifier
            .wrapContentSize()
            .width(70.dp)
            .height(54.dp)
            .background(
                color = sliderBtnBackgroundColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp)
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(sliderBtnIcon),
                contentDescription = "Car Icon",
                modifier = Modifier.size(36.dp)
            )
        }
    }
}
