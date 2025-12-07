package com.pentadigital.calculator.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.ui.theme.CyberpunkTextSecondary

@Composable
fun ConversionVisual(
    fromLabel: String,
    toLabel: String,
    accentColor: Color
) {
    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // From Circle
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                TechText(
                    text = fromLabel.take(1),
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Animated Arrow
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(60.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(3) { index ->
                        // Simple static dots for now, or we can animate them
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(accentColor.copy(alpha = 0.5f))
                        )
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier
                        .offset(x = offset.dp)
                        .alpha(alpha)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // To Circle
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                TechText(
                    text = toLabel.take(1),
                    color = accentColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        TechText(
            text = "CONVERTING...",
            color = CyberpunkTextSecondary,
            fontSize = 12.sp
        )
    }
}
