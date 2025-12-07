package com.pentadigital.calculator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@Composable
fun CalculatorButton(
    symbol: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Unspecified,
    contentColor: Color = Color.White,
    textStyle: androidx.compose.ui.text.TextStyle = androidx.compose.ui.text.TextStyle(),
    fontSize: androidx.compose.ui.unit.TextUnit = 36.sp,
    onClick: () -> Unit
) {
    val haptic = androidx.compose.ui.platform.LocalHapticFeedback.current

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(CircleShape)
            .background(containerColor)
            .clickable {
                haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                onClick()
            }
            .then(modifier)
    ) {
        Text(
            text = symbol,
            fontSize = fontSize,
            color = contentColor,
            style = textStyle
        )
    }
}
