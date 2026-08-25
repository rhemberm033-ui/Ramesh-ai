package com.rameshai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AiOrb(
    modifier: Modifier = Modifier,
    state: Any? = null,
    baseSize: Int = 200,
    isListening: Boolean = false
) {
    AiOrb(
        modifier = modifier,
        state = state,
        baseSize = baseSize.dp,
        isListening = isListening
    )
}

@Composable
fun AiOrb(
    modifier: Modifier = Modifier,
    state: Any? = null,
    baseSize: Dp = 200.dp,
    isListening: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = if (isListening || state != null) 1.2f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = modifier.size(baseSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = (size.minDimension / 2f) * scale
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF6200EE),
                        Color(0xFF03DAC5),
                        Color.Transparent
                    ),
                    radius = radius
                ),
                radius = radius
            )
        }
    }
}
