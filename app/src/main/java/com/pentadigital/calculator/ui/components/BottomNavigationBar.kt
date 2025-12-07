package com.pentadigital.calculator.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.theme.*

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val iconRes: Int,
    val selectedIconRes: Int
) {
    object Home : BottomNavItem(
        route = "home",
        title = "Home",
        iconRes = R.drawable.ic_home,
        selectedIconRes = R.drawable.ic_home
    )
    
    object Favorites : BottomNavItem(
        route = "favorites",
        title = "Favorites",
        iconRes = R.drawable.ic_favorite,
        selectedIconRes = R.drawable.ic_favorite_filled
    )
}

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onCalculatorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(BottomNavItem.Home, BottomNavItem.Favorites)
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(72.dp)
    ) {
        // Bottom Navigation Background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .align(Alignment.BottomCenter),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 16.dp,
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEachIndexed { index, item ->
                    val isSelected = currentRoute == item.route
                    
                    if (index == 1) {
                        // Add spacer in the middle for FAB
                        Spacer(modifier = Modifier.width(72.dp))
                    }
                    
                    BottomNavItemView(
                        item = item,
                        isSelected = isSelected,
                        onClick = { onNavigate(item.route) }
                    )
                }
            }
        }
        
        // Floating Calculator Button
        FloatingActionButton(
            onClick = onCalculatorClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-8).dp)
                .size(64.dp)
                .shadow(12.dp, RoundedCornerShape(18.dp), spotColor = NeonCyan),
            shape = RoundedCornerShape(18.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = NeonCyan,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 8.dp,
                pressedElevation = 12.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, NeonCyan, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_calculator),
                    contentDescription = "Calculator",
                    modifier = Modifier.size(32.dp),
                    tint = NeonCyan
                )
            }
        }
    }
}

@Composable
private fun BottomNavItemView(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "iconColor"
    )
    
    val textColor by animateColorAsState(
        targetValue = if (isSelected) NeonCyan else MaterialTheme.colorScheme.onSurfaceVariant,
        animationSpec = tween(200),
        label = "textColor"
    )
    
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = 32.dp, color = NeonCyan),
                onClick = onClick
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(
                id = if (isSelected) item.selectedIconRes else item.iconRes
            ),
            contentDescription = item.title,
            modifier = Modifier.size(24.dp),
            tint = iconColor
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = item.title.uppercase(),
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            letterSpacing = 1.sp
        )
        
        // Selection indicator
        if (isSelected) {
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(NeonCyan)
                    .shadow(4.dp, spotColor = NeonCyan)
            )
        }
    }
}
