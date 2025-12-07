package com.pentadigital.calculator.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSizeClass {
    COMPACT,    // Phones in portrait
    MEDIUM,     // Small tablets, phones in landscape
    EXPANDED    // Tablets, large screens
}

data class WindowSize(
    val width: WindowSizeClass,
    val height: WindowSizeClass
)

@Composable
fun rememberWindowSize(): WindowSize {
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp
    
    return WindowSize(
        width = getScreenWidthType(screenWidth),
        height = getScreenHeightType(screenHeight)
    )
}

private fun getScreenWidthType(width: Dp): WindowSizeClass {
    return when {
        width < 600.dp -> WindowSizeClass.COMPACT
        width < 840.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

private fun getScreenHeightType(height: Dp): WindowSizeClass {
    return when {
        height < 480.dp -> WindowSizeClass.COMPACT
        height < 900.dp -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
}

// Screen size breakpoints
object ScreenBreakpoints {
    val compactWidth = 600.dp
    val mediumWidth = 840.dp
    
    val compactHeight = 480.dp
    val mediumHeight = 900.dp
}

// Helper composables for responsive sizing
@Composable
fun isCompactScreen(): Boolean {
    val windowSize = rememberWindowSize()
    return windowSize.width == WindowSizeClass.COMPACT
}

@Composable
fun isMediumScreen(): Boolean {
    val windowSize = rememberWindowSize()
    return windowSize.width == WindowSizeClass.MEDIUM
}

@Composable
fun isExpandedScreen(): Boolean {
    val windowSize = rememberWindowSize()
    return windowSize.width == WindowSizeClass.EXPANDED
}

@Composable
fun isLandscape(): Boolean {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp > configuration.screenHeightDp
}
