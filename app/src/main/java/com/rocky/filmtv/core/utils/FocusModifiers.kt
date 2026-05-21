package com.rocky.filmtv.core.utils

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rocky.filmtv.core.theme.PrimaryRed

/**
 * Custom Modifier for TV D-pad focus interaction.
 * Handles scale effect and border glow when the component is focused.
 */
@Composable
fun Modifier.tvFocusScale(
    scaleFactor: Float = 1.08f,
    cornerRadius: Dp = 8.dp,
    focusedBorderColor: Color = Color.White,
    unfocusedBorderColor: Color = Color.Transparent,
    focusedBorderWidth: Dp = 2.dp,
    unfocusedBorderWidth: Dp = 0.dp
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleFactor else 1.0f,
        label = "scale"
    )

    return this
        .focusable(interactionSource = interactionSource)
        .scale(scale)
        .border(
            border = BorderStroke(
                width = if (isFocused) focusedBorderWidth else unfocusedBorderWidth,
                color = if (isFocused) focusedBorderColor else unfocusedBorderColor
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}

/**
 * Focus glow style for highlighted buttons (e.g. Play, Watch Now)
 */
@Composable
fun Modifier.tvFocusGlowButton(
    scaleFactor: Float = 1.05f,
    cornerRadius: Dp = 4.dp
): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isFocused) scaleFactor else 1.0f,
        label = "scale"
    )

    return this
        .focusable(interactionSource = interactionSource)
        .scale(scale)
        .border(
            border = BorderStroke(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) PrimaryRed else Color.DarkGray
            ),
            shape = RoundedCornerShape(cornerRadius)
        )
}
