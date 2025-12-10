package com.pentadigital.calculator.ui.screens

import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.utils.*
import com.pentadigital.calculator.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitConverterScreen(
    state: UnitConverterState,
    onAction: (UnitConverterEvent) -> Unit,
    availableUnits: List<String>,
    onOpenDrawer: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    TechText(
                        stringResource(com.pentadigital.calculator.R.string.unit_converter_title).uppercase(), 
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = stringResource(com.pentadigital.calculator.R.string.back), 
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Tabs
            TabRow(
                selectedTabIndex = state.category.ordinal,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[state.category.ordinal]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                UnitCategory.values().forEach { category ->
                    Tab(
                        selected = state.category == category,
                        onClick = { onAction(UnitConverterEvent.UpdateCategory(category)) },
                        text = { 
                            TechText(
                                stringResource(getLabelForCategory(category)).uppercase(), 
                                color = if (state.category == category) MaterialTheme.colorScheme.primary else CyberpunkTextSecondary,
                                fontWeight = if (state.category == category) FontWeight.Bold else FontWeight.Medium
                            ) 
                        }
                    )
                }
            }

            val isLandscape = isLandscape()
            val windowSize = rememberWindowSize()
            val isTwoPane = isLandscape || windowSize.width == WindowSizeClass.EXPANDED

            if (isTwoPane) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left Pane: Inputs
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Input Amount
                        CyberpunkInput(
                            value = if (state.inputValue == 0.0) "" else if (state.inputValue % 1.0 == 0.0) state.inputValue.toInt().toString() else state.inputValue.toString(),
                            onValueChange = {
                                if (it.isEmpty()) {
                                    onAction(UnitConverterEvent.UpdateInput(0.0))
                                } else {
                                    it.toDoubleOrNull()?.let { num ->
                                        onAction(UnitConverterEvent.UpdateInput(num))
                                    }
                                }
                            },
                            label = stringResource(com.pentadigital.calculator.R.string.value_label).uppercase(),
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = NeonPurple,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = androidx.compose.ui.text.input.ImeAction.Done
                            )
                        )

                        // Unit Selection
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            UnitDropdown(
                                selectedUnit = state.fromUnit,
                                units = availableUnits,
                                onUnitSelected = { onAction(UnitConverterEvent.UpdateFromUnit(it)) }
                            )

                            IconButton(onClick = { onAction(UnitConverterEvent.SwapUnits) }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = stringResource(com.pentadigital.calculator.R.string.swap),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            UnitDropdown(
                                selectedUnit = state.toUnit,
                                units = availableUnits,
                                onUnitSelected = { onAction(UnitConverterEvent.UpdateToUnit(it)) }
                            )
                        }
                    }

                    // Right Pane: Result
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Result Card
                        CyberpunkCard(
                            modifier = Modifier.fillMaxWidth(),
                            borderColor = NeonGreen
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                TechText(
                                    text = stringResource(com.pentadigital.calculator.R.string.result).uppercase(),
                                    color = CyberpunkTextSecondary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                TechText(
                                    text = String.format("%.4f", state.resultValue),
                                    color = NeonGreen,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                TechText(
                                    text = stringResource(getLabelForUnit(state.toUnit)).uppercase(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                
                                ConversionVisual(
                                    fromLabel = state.fromUnit,
                                    toLabel = state.toUnit,
                                    accentColor = NeonGreen
                                )
                            }
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Input Amount
                    CyberpunkInput(
                        value = if (state.inputValue == 0.0) "" else if (state.inputValue % 1.0 == 0.0) state.inputValue.toInt().toString() else state.inputValue.toString(),
                        onValueChange = {
                            if (it.isEmpty()) {
                                onAction(UnitConverterEvent.UpdateInput(0.0))
                            } else {
                                it.toDoubleOrNull()?.let { num ->
                                    onAction(UnitConverterEvent.UpdateInput(num))
                                }
                            }
                        },
                        label = stringResource(com.pentadigital.calculator.R.string.value_label).uppercase(),
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = NeonPurple,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        )
                    )

                    // Unit Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        UnitDropdown(
                            selectedUnit = state.fromUnit,
                            units = availableUnits,
                            onUnitSelected = { onAction(UnitConverterEvent.UpdateFromUnit(it)) }
                        )

                        IconButton(onClick = { onAction(UnitConverterEvent.SwapUnits) }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(com.pentadigital.calculator.R.string.swap),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        UnitDropdown(
                            selectedUnit = state.toUnit,
                            units = availableUnits,
                            onUnitSelected = { onAction(UnitConverterEvent.UpdateToUnit(it)) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Result Card
                    CyberpunkCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = NeonGreen
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TechText(
                                text = stringResource(com.pentadigital.calculator.R.string.result).uppercase(),
                                color = CyberpunkTextSecondary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            TechText(
                                text = String.format("%.4f", state.resultValue),
                                color = NeonGreen,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TechText(
                                text = stringResource(getLabelForUnit(state.toUnit)).uppercase(),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            ConversionVisual(
                                fromLabel = state.fromUnit,
                                toLabel = state.toUnit,
                                accentColor = NeonGreen
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnitDropdown(
    selectedUnit: String,
    units: List<String>,
    onUnitSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clickable { expanded = true }
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.3f), shape = androidx.compose.foundation.shape.CutCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TechText(
                text = stringResource(getLabelForUnit(selectedUnit)),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = stringResource(com.pentadigital.calculator.R.string.dropdown),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface).border(1.dp, NeonPurple, RoundedCornerShape(8.dp))
        ) {
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { TechText(text = stringResource(getLabelForUnit(unit)), color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun getLabelForCategory(category: UnitCategory): Int {
    return when (category) {
        UnitCategory.Length -> com.pentadigital.calculator.R.string.length
        UnitCategory.Weight -> com.pentadigital.calculator.R.string.weight
        UnitCategory.Temperature -> com.pentadigital.calculator.R.string.temperature
    }
}

@Composable
fun getLabelForUnit(unit: String): Int {
    return when (unit) {
        "Meters" -> com.pentadigital.calculator.R.string.unit_meters
        "Kilometers" -> com.pentadigital.calculator.R.string.unit_kilometers
        "Feet" -> com.pentadigital.calculator.R.string.unit_feet
        "Miles" -> com.pentadigital.calculator.R.string.unit_miles
        "Inches" -> com.pentadigital.calculator.R.string.unit_inches
        "Centimeters" -> com.pentadigital.calculator.R.string.unit_centimeters
        "Kilograms" -> com.pentadigital.calculator.R.string.unit_kilograms
        "Grams" -> com.pentadigital.calculator.R.string.unit_grams
        "Pounds" -> com.pentadigital.calculator.R.string.unit_pounds
        "Ounces" -> com.pentadigital.calculator.R.string.unit_ounces
        "Celsius" -> com.pentadigital.calculator.R.string.unit_celsius
        "Fahrenheit" -> com.pentadigital.calculator.R.string.unit_fahrenheit
        "Kelvin" -> com.pentadigital.calculator.R.string.unit_kelvin
        else -> com.pentadigital.calculator.R.string.error // Fallback
    }
}


