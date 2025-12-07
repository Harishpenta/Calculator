package com.pentadigital.calculator.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.pentadigital.calculator.viewmodels.GeometryState
import com.pentadigital.calculator.viewmodels.Shape2D
import com.pentadigital.calculator.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun ShapeVisual(
    state: GeometryState,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CyberpunkDarkBG,
                        Color(0xFF1A1A2E),
                        CyberpunkDarkBG
                    )
                )
            )
            .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
        contentAlignment = Alignment.Center
    ) {
        LayoutGridBackground()

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
        ) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            val maxDimension = minOf(size.width, size.height)
            
            val shapeColor = NeonPurple.copy(alpha = 0.2f)
            val strokeColor = NeonCyan
            val textColor = NeonGreen.toArgb()
            val textPaint = android.graphics.Paint().apply {
                color = textColor
                textSize = 40f
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.MONOSPACE
            }

            fun drawLabel(text: String, x: Float, y: Float) {
                drawContext.canvas.nativeCanvas.drawText(text, x, y, textPaint)
            }

            when (state.selected2DShape) {
                Shape2D.CIRCLE -> {
                    val radius = maxDimension * 0.4f
                    drawCircle(
                        color = shapeColor,
                        radius = radius,
                        center = center
                    )
                    drawCircle(
                        color = strokeColor,
                        radius = radius,
                        center = center,
                        style = Stroke(width = 4f)
                    )
                    drawLine(
                        color = NeonGreen,
                        start = center,
                        end = Offset(centerX + radius, centerY),
                        strokeWidth = 2f
                    )
                    drawLabel("r", centerX + radius / 2, centerY - 10)
                }
                Shape2D.SQUARE -> {
                    val side = maxDimension * 0.6f
                    val halfSide = side / 2
                    val path = Path().apply {
                        moveTo(centerX - halfSide, centerY - halfSide)
                        lineTo(centerX + halfSide, centerY - halfSide)
                        lineTo(centerX + halfSide, centerY + halfSide)
                        lineTo(centerX - halfSide, centerY + halfSide)
                        close()
                    }
                    drawPath(path, shapeColor)
                    drawPath(path, strokeColor, style = Stroke(width = 4f))
                    drawLabel("s", centerX, centerY + halfSide + 40)
                    drawLabel("s", centerX + halfSide + 20, centerY)
                }
                Shape2D.RECTANGLE -> {
                    // Dynamic aspect ratio
                    val inputW = if (state.width > 0) state.width else 1.0
                    val inputL = if (state.length > 0) state.length else 1.5
                    val ratio = (inputL / inputW).toFloat()
                    
                    // Constrain ratio to avoid extreme shapes
                    val constrainedRatio = ratio.coerceIn(0.5f, 2.0f)
                    
                    val baseSize = maxDimension * 0.6f
                    val w: Float
                    val h: Float
                    
                    if (constrainedRatio > 1) {
                        w = baseSize
                        h = baseSize / constrainedRatio
                    } else {
                        h = baseSize
                        w = baseSize * constrainedRatio
                    }
                    
                    val halfW = w / 2
                    val halfH = h / 2

                    val path = Path().apply {
                        moveTo(centerX - halfW, centerY - halfH)
                        lineTo(centerX + halfW, centerY - halfH)
                        lineTo(centerX + halfW, centerY + halfH)
                        lineTo(centerX - halfW, centerY + halfH)
                        close()
                    }
                    drawPath(path, shapeColor)
                    drawPath(path, strokeColor, style = Stroke(width = 4f))
                    drawLabel("L", centerX, centerY + halfH + 40)
                    drawLabel("W", centerX + halfW + 20, centerY)
                }
                Shape2D.TRIANGLE -> {
                    // Simplified triangle visualization (equilateral-ish for general case)
                    val side = maxDimension * 0.7f
                    val height = side * 0.866f // sqrt(3)/2
                    
                    val path = Path().apply {
                        moveTo(centerX, centerY - height / 2)
                        lineTo(centerX + side / 2, centerY + height / 2)
                        lineTo(centerX - side / 2, centerY + height / 2)
                        close()
                    }
                    drawPath(path, shapeColor)
                    drawPath(path, strokeColor, style = Stroke(width = 4f))
                    
                    // Labels for sides (approximate)
                    drawLabel("a", centerX - side / 4 - 20, centerY)
                    drawLabel("b", centerX + side / 4 + 20, centerY)
                    drawLabel("c", centerX, centerY + height / 2 + 40)
                }
                Shape2D.TRAPEZOID -> {
                    val wBottom = maxDimension * 0.8f
                    val wTop = maxDimension * 0.5f
                    val h = maxDimension * 0.5f
                    
                    val path = Path().apply {
                        moveTo(centerX - wTop / 2, centerY - h / 2)
                        lineTo(centerX + wTop / 2, centerY - h / 2)
                        lineTo(centerX + wBottom / 2, centerY + h / 2)
                        lineTo(centerX - wBottom / 2, centerY + h / 2)
                        close()
                    }
                    drawPath(path, shapeColor)
                    drawPath(path, strokeColor, style = Stroke(width = 4f))
                    
                    drawLabel("a", centerX, centerY - h / 2 - 20)
                    drawLabel("b", centerX, centerY + h / 2 + 40)
                    drawLabel("h", centerX, centerY)
                }
                Shape2D.PARALLELOGRAM -> {
                    val w = maxDimension * 0.7f
                    val h = maxDimension * 0.5f
                    val offset = h * 0.5f
                    
                    val path = Path().apply {
                        moveTo(centerX - w / 2 + offset, centerY - h / 2)
                        lineTo(centerX + w / 2 + offset, centerY - h / 2)
                        lineTo(centerX + w / 2 - offset, centerY + h / 2)
                        lineTo(centerX - w / 2 - offset, centerY + h / 2)
                        close()
                    }
                    drawPath(path, shapeColor)
                    drawPath(path, strokeColor, style = Stroke(width = 4f))
                    
                    drawLabel("b", centerX, centerY + h / 2 + 40)
                    drawLabel("h", centerX + w / 2, centerY)
                }
                Shape2D.ELLIPSE -> {
                    val inputA = if (state.semiAxisA > 0) state.semiAxisA else 2.0
                    val inputB = if (state.semiAxisB > 0) state.semiAxisB else 1.0
                    val ratio = (inputA / inputB).toFloat()
                    val constrainedRatio = ratio.coerceIn(0.5f, 2.0f)
                    
                    val baseSize = maxDimension * 0.6f
                    val a: Float
                    val b: Float
                    
                    if (constrainedRatio > 1) {
                        a = baseSize
                        b = baseSize / constrainedRatio
                    } else {
                        b = baseSize
                        a = baseSize * constrainedRatio
                    }

                    drawOval(
                        color = shapeColor,
                        topLeft = Offset(centerX - a, centerY - b),
                        size = Size(a * 2, b * 2)
                    )
                    drawOval(
                        color = strokeColor,
                        topLeft = Offset(centerX - a, centerY - b),
                        size = Size(a * 2, b * 2),
                        style = Stroke(width = 4f)
                    )
                    
                    drawLabel("a", centerX + a / 2, centerY)
                    drawLabel("b", centerX, centerY - b / 2)
                }
                Shape2D.RHOMBUS -> {
                    val d1 = maxDimension * 0.8f
                    val d2 = maxDimension * 0.5f
                    
                    val path = Path().apply {
                        moveTo(centerX, centerY - d2 / 2)
                        lineTo(centerX + d1 / 2, centerY)
                        lineTo(centerX, centerY + d2 / 2)
                        lineTo(centerX - d1 / 2, centerY)
                        close()
                    }
                    drawPath(path, shapeColor)
                    drawPath(path, strokeColor, style = Stroke(width = 4f))
                    
                    drawLabel("d1", centerX + d1 / 4, centerY - 10)
                    drawLabel("d2", centerX - 10, centerY - d2 / 4)
                }
            }
        }
    }
}

@Composable
private fun LayoutGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacing = 40.dp.toPx()
        val gridColor = NeonCyan.copy(alpha = 0.05f)
        
        // Draw vertical lines
        for (x in 0..size.width.toInt() step spacing.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(x.toFloat(), 0f),
                end = Offset(x.toFloat(), size.height),
                strokeWidth = 1f
            )
        }
        // Draw horizontal lines
        for (y in 0..size.height.toInt() step spacing.toInt()) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y.toFloat()),
                end = Offset(size.width, y.toFloat()),
                strokeWidth = 1f
            )
        }
    }
}
