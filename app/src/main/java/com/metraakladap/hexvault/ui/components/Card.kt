package com.metraakladap.hexvault.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun ElevatedRoundedCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    elevation: Int = 8,
    content: @Composable () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    val interactionSource = remember { MutableInteractionSource() }
    val isClickable = onClick != null
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isClickable && isPressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "cardPressScale"
    )

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn(animationSpec = tween(300)) + scaleIn(
            initialScale = 0.95f,
            animationSpec = tween(300)
        ),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(
            targetScale = 0.95f,
            animationSpec = tween(200)
        )
    ) {
        Card(
            modifier = modifier
                .then(
                    if (isClickable) Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onClick?.invoke() } else Modifier
                )
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .animateContentSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                content()
            }
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val visible = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible.value = true }

    val interactionSource = remember { MutableInteractionSource() }
    val isClickable = onClick != null
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressScale by animateFloatAsState(
        targetValue = if (isClickable && isPressed) 0.98f else 1f,
        animationSpec = tween(100),
        label = "glassCardPressScale"
    )

    AnimatedVisibility(
        visible = visible.value,
        enter = fadeIn(animationSpec = tween(400)) + scaleIn(
            initialScale = 0.9f,
            animationSpec = tween(400)
        ),
        exit = fadeOut(animationSpec = tween(200)) + scaleOut(
            targetScale = 0.9f,
            animationSpec = tween(200)
        )
    ) {
        Card(
            modifier = modifier
                .then(
                    if (isClickable) Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) { onClick?.invoke() } else Modifier
                )
                .graphicsLayer {
                    scaleX = pressScale
                    scaleY = pressScale
                }
                .animateContentSize()
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(20.dp)
                ),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f)
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(24.dp)
            ) {
                content()
            }
        }
    }
}


