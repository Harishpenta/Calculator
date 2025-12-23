package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.ui.theme.*
import com.pentadigital.calculator.utils.isLandscape
import com.pentadigital.calculator.viewmodels.TipEvent
import com.pentadigital.calculator.viewmodels.TipState
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.foundation.layout.WindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipScreen(
    state: TipState,
    onAction: (TipEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.tip_calculator_title).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TipInputs(state, onAction)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TipResults(state)
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                TipInputs(state, onAction)
                TipResults(state)
            }
        }
    }
}

@Composable
private fun TipInputs(
    state: TipState,
    onAction: (TipEvent) -> Unit
) {
    val billFocus = remember { FocusRequester() }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            CyberpunkInput(
                value = if (state.billAmount == 0.0) "" else if (state.billAmount % 1.0 == 0.0) state.billAmount.toInt().toString() else state.billAmount.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(TipEvent.UpdateBillAmount(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(TipEvent.UpdateBillAmount(num))
                        }
                    }
                },
                label = stringResource(R.string.bill_amount).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(billFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            // Tip Percentage Slider and Chips
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TechText(
                    text = "${stringResource(R.string.tip_percentage).uppercase()}: ${state.tipPercentage.toInt()}%",
                    fontSize = 14.sp,
                    color = CyberpunkTextSecondary
                )
                Slider(
                    value = state.tipPercentage,
                    onValueChange = { onAction(TipEvent.UpdateTipPercentage(it)) },
                    valueRange = 0f..50f,
                    steps = 49,
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf(10f, 15f, 20f).forEach { percent ->
                        FilterChip(
                            selected = state.tipPercentage == percent,
                            onClick = { onAction(TipEvent.UpdateTipPercentage(percent)) },
                            label = { TechText("${percent.toInt()}%", color = if (state.tipPercentage == percent) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.secondary) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.secondary,
                                selectedLabelColor = MaterialTheme.colorScheme.onSurface,
                                containerColor = Color.Transparent,
                                labelColor = MaterialTheme.colorScheme.secondary
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = state.tipPercentage == percent,
                                borderColor = MaterialTheme.colorScheme.secondary,
                                selectedBorderColor = MaterialTheme.colorScheme.secondary
                            )
                        )
                    }
                }
            }

            // Split Count
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TechText(
                    text = stringResource(R.string.split_count).uppercase(),
                    fontSize = 14.sp,
                    color = CyberpunkTextSecondary
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onAction(TipEvent.UpdateSplitCount(state.splitCount - 1)) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_remove),
                            contentDescription = "Decrease split",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    TechText(
                        text = state.splitCount.toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    IconButton(
                        onClick = { onAction(TipEvent.UpdateSplitCount(state.splitCount + 1)) },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase split", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun TipResults(state: TipState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TechText(
                text = stringResource(R.string.per_person).uppercase(),
                color = CyberpunkTextSecondary,
                fontSize = 14.sp
            )
            TechText(
                text = currencyFormat.format(state.amountPerPerson),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResultRow(
                label = stringResource(R.string.total_tip).uppercase(),
                value = currencyFormat.format(state.tipAmount),
                valueColor = CyberpunkTextSecondary
            )
            ResultRow(
                label = stringResource(R.string.total_bill).uppercase(),
                value = currencyFormat.format(state.totalAmount),
                valueColor = MaterialTheme.colorScheme.secondary,
                isBold = true
            )
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TechText(
            text = label,
            color = CyberpunkTextSecondary,
            fontSize = 14.sp
        )
        TechText(
            text = value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
    }
}
