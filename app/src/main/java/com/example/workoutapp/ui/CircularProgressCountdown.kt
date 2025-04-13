package com.example.workoutapp.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularProgressCountdown(durationSeconds: Int, timeLeft: Int, isRunning: Boolean, countdownId: String) {
    val progress = remember { Animatable(1f) }


    LaunchedEffect(countdownId) {
        if (isRunning) {
            progress.snapTo(1f)
            progress.animateTo(
                targetValue = 0f,
                animationSpec = tween(
                    durationMillis = durationSeconds * 1000,
                    easing = LinearEasing
                )
            )
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(150.dp)
    ) {
        CircularProgressIndicator(
            progress = progress.value,
            modifier = Modifier.size(150.dp),
            strokeWidth = 16.dp,
            color = lerp(Color.Green, Color.Red, 1f - progress.value)
        )

        Text(
            text = "$timeLeft s",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}