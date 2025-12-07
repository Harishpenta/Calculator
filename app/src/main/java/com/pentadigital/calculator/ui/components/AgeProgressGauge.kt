package com.pentadigital.calculator.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.ui.theme.CyberpunkTextSecondary
import com.pentadigital.calculator.ui.theme.NeonCyan
import com.pentadigital.calculator.ui.theme.NeonGreen

@Composable
fun AgeProgressGauge(
    daysToNextBirthday: Int,
    totalDaysInYear: Int = 365,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 16.dp,
    animDuration: Int = 1000
) {
    val progress = 1f - (daysToNextBirthday.toFloat() / totalDaysInYear.toFloat())
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress,
            animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val canvasSize = this.size.minDimension
            val stroke = strokeWidth.toPx()
            val arcSize = Size(canvasSize - stroke, canvasSize - stroke)
            val topLeft = Offset(stroke / 2, stroke / 2)

            // Background Track
            drawArc(
                color = NeonCyan.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Progress Arc
            drawArc(
                color = NeonGreen,
                startAngle = -90f, // Start from top
                sweepAngle = animatedProgress.value * 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TechText(
                text = "$daysToNextBirthday",
                color = NeonGreen,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            TechText(
                text = "DAYS TO GO",
                color = CyberpunkTextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
