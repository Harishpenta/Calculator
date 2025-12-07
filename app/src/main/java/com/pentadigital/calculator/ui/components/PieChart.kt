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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.ui.theme.TextPrimaryLight
import com.pentadigital.calculator.ui.theme.TextSecondaryLight

data class PieChartData(
    val value: Double,
    val color: Color,
    val label: String
)

@Composable
fun PieChart(
    data: List<PieChartData>,
    modifier: Modifier = Modifier,
    chartSize: Dp = 200.dp,
    strokeWidth: Dp = 24.dp,
    animDuration: Int = 1000
) {
    val totalValue = data.sumOf { it.value }
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(data) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(chartSize)
        ) {
            Canvas(modifier = Modifier.size(chartSize)) {
                val canvasSize = size.minDimension
                val radius = canvasSize / 2
                val stroke = strokeWidth.toPx()
                val center = Offset(canvasSize / 2, canvasSize / 2)
                val arcSize = Size(canvasSize - stroke, canvasSize - stroke)
                val topLeft = Offset(stroke / 2, stroke / 2)

                var startAngle = -90f

                data.forEach { item ->
                    val sweepAngle = (item.value / totalValue * 360f).toFloat() * animatedProgress.value
                    
                    drawArc(
                        color = item.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = stroke, cap = StrokeCap.Round)
                    )
                    
                    startAngle += sweepAngle
                }
            }
        }

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            data.forEachIndexed { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(item.color)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = item.label,
                            color = TextSecondaryLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
