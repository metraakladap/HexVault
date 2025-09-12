package com.metraakladap.hexvault.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

@Composable
fun GradientBackground(content: @Composable () -> Unit) {
    val transition = rememberInfiniteTransition(label = "backgroundGradient")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 15000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientPhase"
    )

    // Bitcoin-inspired gradient colors
    val bitcoinOrange = Color(0xFFF7931A)
    val darkBlue = Color(0xFF1A1A2E)
    val deepPurple = Color(0xFF16213E)
    val teal = Color(0xFF0F3460)
    val lightBlue = Color(0xFF533A7B)
    
    // Create multiple gradient phases for more dynamic effect
    val topColor = when {
        phase < 0.25f -> lerp(darkBlue, deepPurple, phase * 4)
        phase < 0.5f -> lerp(deepPurple, teal, (phase - 0.25f) * 4)
        phase < 0.75f -> lerp(teal, lightBlue, (phase - 0.5f) * 4)
        else -> lerp(lightBlue, darkBlue, (phase - 0.75f) * 4)
    }
    
    val bottomColor = when {
        phase < 0.25f -> lerp(deepPurple, teal, phase * 4)
        phase < 0.5f -> lerp(teal, lightBlue, (phase - 0.25f) * 4)
        phase < 0.75f -> lerp(lightBlue, darkBlue, (phase - 0.5f) * 4)
        else -> lerp(darkBlue, deepPurple, (phase - 0.75f) * 4)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        topColor,
                        bottomColor,
                        topColor.copy(alpha = 0.8f)
                    ),
                    startY = 0f,
                    endY = Float.POSITIVE_INFINITY
                )
            )
    ) {
        content()
    }
}

@Composable
fun StaticGradientBackground(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1A1A2E),
                        Color(0xFF16213E),
                        Color(0xFF0F3460)
                    )
                )
            )
    ) {
        content()
    }
}


