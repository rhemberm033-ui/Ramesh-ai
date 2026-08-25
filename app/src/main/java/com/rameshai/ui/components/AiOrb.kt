package com.rameshai.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rameshai.model.VoiceState

/**
 * Central animated orb. Pulses gently when idle/thinking, and pulses
 * faster + brighter while listening or speaking.
 */
@Composable
fun AiOrb(state: VoiceState, modifier: Modifier = Modifier, baseSize: Int = 140) {
    val infinite = rememberInfiniteTransition(label = "orb")
    val speedMs = when (state) {
        VoiceState.LISTENING -> 700
        VoiceState.SPEAKING -> 500
        VoiceState.THINKING -> 900
        VoiceState.IDLE -> 1800
    }
    val scale by infinite.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(speedMs, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    val colors = when (state) {
        VoiceState.LISTENING -> listOf(Color(0xFF7C4DFF), Color(0xFF00E5FF))
        VoiceState.SPEAKING -> listOf(Color(0xFF448AFF), Color(0xFF7C4DFF))
        VoiceState.THINKING -> listOf(Color(0xFF7C4DFF), Color(0xFF3D5AFE))
        VoiceState.IDLE -> listOf(Color(0xFF5C4DFF), Color(0xFF448AFF))
    }

    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .size((baseSize * scale).dp)
            .blur(2.dp)
            .background(
                brush = Brush.radialGradient(colors),
                shape = androidx.compose.foundation.shape.CircleShape
            )
    )
}
