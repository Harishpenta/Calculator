package com.pentadigital.calculator.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.ui.theme.NeonCyan
import com.pentadigital.calculator.ui.theme.NeonGreen
import com.pentadigital.calculator.ui.theme.NeonPurple
import androidx.compose.material3.MaterialTheme
import kotlin.math.roundToInt

@Composable
fun CyberpunkWeightGauge(
    value: Float,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier,
    range: ClosedFloatingPointRange<Float> = 10f..300f,
    unit: String = "kg"
) {
    var dragOffset by remember { mutableStateOf(0f) }
    val tickColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val valueColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val updatedValue by rememberUpdatedState(value)

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->
                change.consume()
                // Adjust sensitivity
                val newValue = (updatedValue - dragAmount * 0.1f).coerceIn(range)
                onValueChange(newValue.toDouble())
            }
        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerX = width / 2
            
            // Draw center indicator
            drawLine(
                color = primaryColor,
                start = Offset(centerX, 0f),
                end = Offset(centerX, height),
                strokeWidth = 4.dp.toPx()
            )
            
            // Draw ticks
            val visibleRange = 20 // visible range +/-
            val pxPerUnit = width / (visibleRange * 2)
            
            for (i in (value - visibleRange).toInt()..(value + visibleRange).toInt()) {
                val offset = (i - value) * pxPerUnit
                val x = centerX + offset
                
                if (x in 0f..width) {
                    val isMajor = i % 5 == 0
                    val tickHeight = if (isMajor) height * 0.5f else height * 0.25f
                    val color = if (isMajor) secondaryColor else secondaryColor.copy(alpha = 0.5f)
                    
                    drawLine(
                        color = color,
                        start = Offset(x, height - tickHeight),
                        end = Offset(x, height),
                        strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                    )
                    
                    if (isMajor) {
                        drawContext.canvas.nativeCanvas.drawText(
                            i.toString(),
                            x,
                            height - tickHeight - 20,
                            android.graphics.Paint().apply {
                                this.color = tickColor
                                this.textSize = 30f
                                this.textAlign = android.graphics.Paint.Align.CENTER
                            }
                        )
                    }
                }
            }
            
            // Draw current value text large
            drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1f %s", value, unit),
                centerX,
                height * 0.3f,
                android.graphics.Paint().apply {
                    this.color = valueColor
                    this.textSize = 60f
                    this.textAlign = android.graphics.Paint.Align.CENTER
                    this.isFakeBoldText = true
                }
            )
        }
    }
}

@Composable
fun CyberpunkHeightRuler(
    value: Float,
    onValueChange: (Double) -> Unit,
    modifier: Modifier = Modifier
) {
    val tickColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val valueColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val updatedValue by rememberUpdatedState(value)

    Box(
        modifier = modifier.pointerInput(Unit) {
            detectVerticalDragGestures { change, dragAmount ->
                change.consume()
                val newValue = (updatedValue + dragAmount * 0.1f).coerceIn(50f, 250f)
                onValueChange(newValue.toDouble())
            }
        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val centerY = height / 2
            
            // Draw center indicator line
            drawLine(
                color = secondaryColor,
                start = Offset(0f, centerY),
                end = Offset(width, centerY),
                strokeWidth = 4.dp.toPx()
            )
            
            // Draw ticks
            val range = 40 // visible range +/-
            val pxPerUnit = height / (range * 2)
            
            for (i in (value - range).toInt()..(value + range).toInt()) {
                val offset = (i - value) * pxPerUnit
                val y = centerY + offset // + because y goes down
                
                if (y in 0f..height) {
                    val isMajor = i % 10 == 0
                    val tickWidth = if (isMajor) width * 0.5f else width * 0.25f
                    val color = if (isMajor) primaryColor else primaryColor.copy(alpha = 0.5f)
                    
                    drawLine(
                        color = color,
                        start = Offset(width - tickWidth, y),
                        end = Offset(width, y),
                        strokeWidth = if (isMajor) 2.dp.toPx() else 1.dp.toPx()
                    )
                    
                    if (isMajor) {
                        drawContext.canvas.nativeCanvas.drawText(
                            i.toString(),
                            width - tickWidth - 40,
                            y + 10,
                            android.graphics.Paint().apply {
                                this.color = tickColor
                                this.textSize = 30f
                                this.textAlign = android.graphics.Paint.Align.RIGHT
                            }
                        )
                    }
                }
            }
            
            // Draw current value
             drawContext.canvas.nativeCanvas.drawText(
                String.format("%.1f cm", value),
                width * 0.2f,
                centerY + 15,
                android.graphics.Paint().apply {
                    this.color = valueColor
                    this.textSize = 60f
                    this.textAlign = android.graphics.Paint.Align.LEFT
                    this.isFakeBoldText = true
                }
            )
        }
    }
}

@Composable
fun CyberpunkResultBar(
    bmi: Float,
    category: String,
    modifier: Modifier = Modifier
) {
    val barHeight = 24.dp
    val markerColor = MaterialTheme.colorScheme.onSurface
    val secondaryColor = MaterialTheme.colorScheme.secondary
    
    Box(modifier = modifier.height(barHeight * 3)) { // Ensure space for marker
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = barHeight.toPx()
            
            // Draw segments
            // Underweight < 18.5 (Blue)
            // Normal 18.5 - 25 (Green)
            // Overweight 25 - 30 (Orange)
            // Obese > 30 (Red)
            
            val totalRange = 40f // 0 to 40
            val scale = w / totalRange
            
            val uwEnd = 18.5f * scale
            val normEnd = 25f * scale
            val owEnd = 30f * scale
            
            drawRect(color = Color(0xFF00E5FF), size = androidx.compose.ui.geometry.Size(uwEnd, h), topLeft = Offset(0f, h))
            drawRect(color = secondaryColor, size = androidx.compose.ui.geometry.Size(normEnd - uwEnd, h), topLeft = Offset(uwEnd, h))
            drawRect(color = Color(0xFFFFB74D), size = androidx.compose.ui.geometry.Size(owEnd - normEnd, h), topLeft = Offset(normEnd, h))
            drawRect(color = Color(0xFFFF5252), size = androidx.compose.ui.geometry.Size(w - owEnd, h), topLeft = Offset(owEnd, h))
            
            // Draw Marker
            val bmiClamped = bmi.coerceIn(0f, 40f)
            val markerX = bmiClamped * scale
            
            drawLine(
                color = markerColor,
                start = Offset(markerX, 0f),
                end = Offset(markerX, h * 2.5f),
                strokeWidth = 4.dp.toPx()
            )
            
            // Draw category text below
            /*drawContext.canvas.nativeCanvas.drawText(
                category,
                markerX,
                h * 3,
                android.graphics.Paint().apply {
                    this.color = android.graphics.Color.WHITE
                    this.textSize = 30f
                    this.textAlign = android.graphics.Paint.Align.CENTER
                }
            )*/
        }
    }
}
