package com.pentadigital.calculator.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.ui.theme.FinanceGreenDark
import com.pentadigital.calculator.ui.theme.TextSecondaryLight
import kotlin.math.min

@Composable
fun BmiGauge(
    bmiValue: Double,
    modifier: Modifier = Modifier,
    gaugeSize: Dp = 250.dp,
    strokeWidth: Dp = 24.dp,
    animDuration: Int = 1000
) {
    val animatedBmi = remember { Animatable(0f) }

    LaunchedEffect(bmiValue) {
        animatedBmi.animateTo(
            targetValue = bmiValue.toFloat(),
            animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
        )
    }

    // BMI Categories and their ranges (simplified for visualization)
    // 0-18.5: Underweight (Blue)
    // 18.5-25: Normal (Green)
    // 25-30: Overweight (Orange)
    // 30-40+: Obese (Red)
    // We map 0-40 BMI to 180 degrees
    val maxBmi = 40f
    
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier.size(width = gaugeSize, height = gaugeSize / 2 + 32.dp)
        ) {
            Canvas(modifier = Modifier.size(gaugeSize)) {
                val canvasSize = size.minDimension
                val stroke = strokeWidth.toPx()
                val arcSize = Size(canvasSize - stroke, canvasSize - stroke)
                val topLeft = Offset(stroke / 2, stroke / 2)

                // Draw Segments
                // Total 180 degrees
                // Underweight: 0 to 18.5 -> (18.5 / 40) * 180 = 83.25 deg
                // Normal: 18.5 to 25 -> (6.5 / 40) * 180 = 29.25 deg
                // Overweight: 25 to 30 -> (5 / 40) * 180 = 22.5 deg
                // Obese: 30 to 40 -> (10 / 40) * 180 = 45 deg

                val segments = listOf(
                    Triple(Color(0xFF64B5F6), 0f, 18.5f),      // Underweight
                    Triple(FinanceGreenDark, 18.5f, 25f),      // Normal
                    Triple(Color(0xFFFFB74D), 25f, 30f),       // Overweight
                    Triple(Color(0xFFE57373), 30f, 40f)        // Obese
                )

                var currentAngle = 180f // Start from left (180 degrees in standard coordinate system)

                segments.forEach { (color, startBmi, endBmi) ->
                    val sweep = ((endBmi - startBmi) / maxBmi * 180f)
                    
                    drawArc(
                        color = color,
                        startAngle = currentAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Butt)
                    )
                    currentAngle += sweep
                }

                // Draw Needle
                val clampedBmi = min(animatedBmi.value, maxBmi)
                val needleAngle = (clampedBmi / maxBmi * 180f) + 180f
                
                val center = Offset(canvasSize / 2, canvasSize / 2)
                val needleLength = (canvasSize / 2) - stroke - 10.dp.toPx()

                rotate(degrees = needleAngle, pivot = center) {
                    drawLine(
                        color = TextSecondaryLight,
                        start = center,
                        end = Offset(center.x + needleLength, center.y),
                        strokeWidth = 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                
                // Draw Pivot
                drawCircle(
                    color = TextSecondaryLight,
                    radius = 8.dp.toPx(),
                    center = center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            LegendItem(Color(0xFF64B5F6), "Under")
            LegendItem(FinanceGreenDark, "Normal")
            LegendItem(Color(0xFFFFB74D), "Over")
            LegendItem(Color(0xFFE57373), "Obese")
        }
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, androidx.compose.foundation.shape.CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = TextSecondaryLight,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
