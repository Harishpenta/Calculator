package com.pentadigital.calculator.ui.screens

import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.utils.*
import com.pentadigital.calculator.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showHistory by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    if (showHistory) {
        ModalBottomSheet(
            onDismissRequest = { showHistory = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TechText(
                    text = androidx.compose.ui.res.stringResource(com.pentadigital.calculator.R.string.history_title).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                if (state.history.isEmpty()) {
                    TechText(
                        text = androidx.compose.ui.res.stringResource(com.pentadigital.calculator.R.string.no_history),
                        color = CyberpunkTextSecondary,
                        modifier = Modifier.padding(32.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false)
                            .height(300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.history.reversed()) { item ->
                            CyberpunkCard(
                                borderColor = NeonPurple.copy(alpha=0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                TechText(
                                    text = item,
                                    fontSize = 16.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    textAlign = TextAlign.End
                                )
                            }
                        }
                    }
                    
                    // Export Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CyberpunkButton(
                            text = stringResource(R.string.export_pdf).uppercase(),
                            onClick = {
                                val result = ExportUtils.exportHistoryToPdf(context, state.history)
                                result.onSuccess { file -> ExportUtils.shareFile(context, file) }
                            },
                            modifier = Modifier.weight(1f),
                            color = NeonPurple
                        )
                        
                        CyberpunkButton(
                            text = stringResource(R.string.export_csv).uppercase(),
                            onClick = {
                                val result = ExportUtils.exportHistoryToCsv(context, state.history)
                                result.onSuccess { file -> ExportUtils.shareFile(context, file) }
                            },
                            modifier = Modifier.weight(1f),
                            color = NeonPurple
                        )
                    }
                    
                    CyberpunkButton(
                        text = stringResource(R.string.clear_history).uppercase(),
                        onClick = { 
                            onAction(CalculatorAction.ClearHistory)
                            showHistory = false 
                        },
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }

    val windowSize = rememberWindowSize()
    val isLandscape = isLandscape()
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.dp
    
    val maxWidth = when {
        windowSize.width == WindowSizeClass.EXPANDED && !isLandscape -> 600.dp
        windowSize.width == WindowSizeClass.EXPANDED && isLandscape -> 800.dp
        windowSize.width == WindowSizeClass.MEDIUM -> 500.dp
        else -> screenWidthDp
    }
    
    val horizontalPadding = when {
        windowSize.width == WindowSizeClass.EXPANDED -> 32.dp
        windowSize.width == WindowSizeClass.MEDIUM -> 24.dp
        else -> 16.dp
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding, vertical = 8.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = { showHistory = true }) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = stringResource(R.string.history_title),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = if (windowSize.width == WindowSizeClass.EXPANDED) 1000.dp else maxWidth)
                    .align(Alignment.Center)
                    .padding(horizontal = horizontalPadding)
                    .padding(top = 16.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight()
                        .padding(end = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    DisplayArea(state = state, modifier = Modifier.fillMaxWidth())
                }
                
                Box(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    ButtonsGrid(
                        state = state,
                        onAction = onAction,
                        modifier = Modifier.fillMaxWidth(),
                        buttonSpacing = when {
                            windowSize.width == WindowSizeClass.EXPANDED -> 12.dp
                            windowSize.width == WindowSizeClass.MEDIUM -> 8.dp
                            else -> 6.dp
                        }
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = maxWidth)
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = horizontalPadding)
                    .padding(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DisplayArea(state = state, modifier = Modifier.fillMaxWidth())
                ButtonsGrid(state = state, onAction = onAction, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

// Reusable Responsive Text Component
@Composable
fun ResponsiveText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    maxFontSize: androidx.compose.ui.unit.TextUnit,
    minFontSize: androidx.compose.ui.unit.TextUnit = 12.sp,
    fontWeight: FontWeight = FontWeight.Normal,
    textAlign: TextAlign = TextAlign.End
) {
    BoxWithConstraints(modifier = modifier) {
        val density = androidx.compose.ui.platform.LocalDensity.current
        
        // Estimation: Monospace char width is roughly 0.6 * fontSize
        val estimatedCharWidthRatio = 0.6f 
        
        val calculatedFontSize = with(density) {
            val availableWidthPx = maxWidth.toPx()
            val charCount = text.length.coerceAtLeast(1)
            // Safety buffer of 0.9 to prevent edge touching
            val maxFontSizePx = (availableWidthPx * 0.9f) / (charCount * estimatedCharWidthRatio)
            maxFontSizePx.toSp()
        }
        
        val finalFontSize = if (calculatedFontSize > maxFontSize) maxFontSize 
                            else if (calculatedFontSize < minFontSize) minFontSize 
                            else calculatedFontSize

        TechText(
            text = text,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth(),
            fontWeight = fontWeight,
            fontSize = finalFontSize,
            color = color,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
private fun DisplayArea(
    state: CalculatorState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .padding(end = 16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        if (state.memory.isNotBlank()) {
            TechText(
                text = "M: ${formatNumber(state.memory)}",
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 16.sp,
                color = NeonPurple,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
        
        if (state.errorMessage != null) {
            TechText(
                text = state.errorMessage,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth(),
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.error,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Secondary Text (Formula) - Now Responsive
        val secondaryText = if (state.operation != null) "${formatNumber(state.number1)} ${state.operation.symbol}" else ""
        ResponsiveText(
            text = secondaryText,
            modifier = Modifier.fillMaxWidth(),
            maxFontSize = 32.sp,
            minFontSize = 16.sp,
            color = CyberpunkTextSecondary
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        val mainText = when {
            state.errorMessage != null -> stringResource(R.string.error)
            state.number2.isNotBlank() -> formatNumber(state.number2)
            state.number1.isNotBlank() -> formatNumber(state.number1)
            else -> "0"
        }
        
        // Main Text - Responsive
        ResponsiveText(
            text = mainText,
            modifier = Modifier.fillMaxWidth(),
            maxFontSize = 64.sp,
            minFontSize = 20.sp,
            fontWeight = FontWeight.Light,
            color = if (state.errorMessage != null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun ButtonsGrid(
    state: CalculatorState,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier,
    buttonSpacing: Dp = 10.dp
) {
    val isLandscape = isLandscape()
    val fontSize = if (isLandscape) 24.sp else 32.sp
    
    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(if (isLandscape) Modifier.fillMaxHeight() else Modifier),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        val rowModifier = Modifier
            .fillMaxWidth()
            .then(if (isLandscape) Modifier.weight(1f) else Modifier)

        fun Modifier.buttonModifier(weight: Float = 1f): Modifier {
            return this
                .then(if (!isLandscape) Modifier.aspectRatio(weight * 1.1f) else Modifier.fillMaxHeight())
                .weight(weight)
        }

        // Row 1: Scientific
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            val opsBtnColor = NeonPurple.copy(alpha = 0.15f)
            val opsBorderColor = NeonPurple.copy(alpha=0.5f)
            
            CyberpunkButton(symbol = "√", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.SquareRoot) })
            CyberpunkButton(symbol = "x²", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Square) })
            CyberpunkButton(symbol = "%", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Percent) })
            CyberpunkButton(symbol = "±", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Negate) })
        }
        
        // Row 2: Clear/Del/Div
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CyberpunkButton(symbol = "AC", modifier = Modifier.buttonModifier(), textColor = MaterialTheme.colorScheme.error, fontSize = fontSize, onClick = { onAction(CalculatorAction.Clear) })
            CyberpunkButton(symbol = "Del", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Delete) })
            CyberpunkButton(symbol = "/", modifier = Modifier.buttonModifier(2f), textColor = MaterialTheme.colorScheme.secondary, fontSize = fontSize, onClick = { onAction(CalculatorAction.Operation(CalculatorOperation.Divide)) })
        }
        
        // Row 3: 7,8,9,x
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CyberpunkButton(symbol = "7", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(7)) })
            CyberpunkButton(symbol = "8", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(8)) })
            CyberpunkButton(symbol = "9", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(9)) })
            CyberpunkButton(symbol = "×", modifier = Modifier.buttonModifier(), textColor = MaterialTheme.colorScheme.secondary, fontSize = fontSize, onClick = { onAction(CalculatorAction.Operation(CalculatorOperation.Multiply)) })
        }
        
        // Row 4: 4,5,6,-
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CyberpunkButton(symbol = "4", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(4)) })
            CyberpunkButton(symbol = "5", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(5)) })
            CyberpunkButton(symbol = "6", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(6)) })
            CyberpunkButton(symbol = "-", modifier = Modifier.buttonModifier(), textColor = MaterialTheme.colorScheme.secondary, fontSize = fontSize, onClick = { onAction(CalculatorAction.Operation(CalculatorOperation.Subtract)) })
        }
        
        // Row 5: 1,2,3,+
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CyberpunkButton(symbol = "1", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(1)) })
            CyberpunkButton(symbol = "2", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(2)) })
            CyberpunkButton(symbol = "3", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(3)) })
            CyberpunkButton(symbol = "+", modifier = Modifier.buttonModifier(), textColor = MaterialTheme.colorScheme.secondary, fontSize = fontSize, onClick = { onAction(CalculatorAction.Operation(CalculatorOperation.Add)) })
        }
        
        // Row 6: 0, ., =
        Row(modifier = rowModifier, horizontalArrangement = Arrangement.spacedBy(buttonSpacing)) {
            CyberpunkButton(symbol = "0", modifier = Modifier.buttonModifier(2f), fontSize = fontSize, onClick = { onAction(CalculatorAction.Number(0)) })
            CyberpunkButton(symbol = ".", modifier = Modifier.buttonModifier(), fontSize = fontSize, onClick = { onAction(CalculatorAction.Decimal) })
            CyberpunkButton(symbol = "=", modifier = Modifier.buttonModifier(), textColor = MaterialTheme.colorScheme.primary, fontSize = fontSize, onClick = { onAction(CalculatorAction.Calculate) })
        }
    }
}

@Composable
private fun CyberpunkButton(
    symbol: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Transparent,
    borderColor: Color = NeonPurple.copy(alpha = 0.5f),
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    fontSize: androidx.compose.ui.unit.TextUnit,
    onClick: () -> Unit
) {
    com.pentadigital.calculator.ui.components.CyberpunkButton(
        text = symbol,
        onClick = onClick,
        modifier = modifier,
        color = textColor
    )
}

fun formatNumber(number: String): String {
    if (number.isEmpty()) return ""
    if (number == ".") return "0."
    val isNegative = number.startsWith("-")
    val cleanNumber = if (isNegative) number.substring(1) else number
    val parts = cleanNumber.split(".")
    val integerPart = parts[0].toLongOrNull() ?: return number
    val formattedInteger = java.text.NumberFormat.getInstance().format(integerPart)
    val result = if (parts.size > 1) "$formattedInteger.${parts[1]}" else if (cleanNumber.endsWith(".")) "$formattedInteger." else formattedInteger
    return if (isNegative) "-$result" else result
}
