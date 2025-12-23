package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.theme.*
import com.pentadigital.calculator.ui.components.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.pentadigital.calculator.utils.isLandscape
import com.pentadigital.calculator.viewmodels.ActivityLevel
import com.pentadigital.calculator.viewmodels.Climate
import com.pentadigital.calculator.viewmodels.WaterIntakeEvent
import com.pentadigital.calculator.viewmodels.WaterIntakeState

import androidx.compose.foundation.layout.WindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterIntakeScreen(
    state: WaterIntakeState,
    onAction: (WaterIntakeEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.water_intake_title).uppercase(),
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
                    WaterIntakeInputs(state, onAction)
                    CyberpunkButton(
                        onClick = { onAction(WaterIntakeEvent.Calculate) },
                        text = stringResource(R.string.calculate),
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    WaterIntakeResults(state)
                }
            }
        } else {
            val scrollState = rememberScrollState()

            LaunchedEffect(state.waterIntakeLiters) {
                if (state.waterIntakeLiters > 0) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                WaterIntakeInputs(state, onAction)
                CyberpunkButton(
                    onClick = { onAction(WaterIntakeEvent.Calculate) },
                    text = stringResource(R.string.calculate),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                WaterIntakeResults(state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WaterIntakeInputs(
    state: WaterIntakeState,
    onAction: (WaterIntakeEvent) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    CyberpunkCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = NeonPurple
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Weight
            TechText(
                text = stringResource(R.string.weight_kg).uppercase(),
                color = CyberpunkTextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            CyberpunkWeightGauge(
                value = state.weight.toFloatOrNull() ?: 70f,
                onValueChange = { onAction(WaterIntakeEvent.UpdateWeight(String.format("%.1f", it))) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                    .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Activity Level Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.activityLevel.name.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { TechText(stringResource(R.string.activity_level), color = CyberpunkTextSecondary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = CyberpunkTextSecondary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = CyberpunkTextPrimary,
                        unfocusedTextColor = CyberpunkTextPrimary,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    ),
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    ActivityLevel.values().forEach { level ->
                        DropdownMenuItem(
                            text = { TechText(level.name.replace("_", " ").lowercase().replaceFirstChar { it.titlecase() }, color = CyberpunkTextPrimary) },
                            onClick = {
                                onAction(WaterIntakeEvent.UpdateActivityLevel(level))
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Climate
            TechText(
                text = stringResource(R.string.climate).uppercase(),
                color = CyberpunkTextSecondary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ClimateButton(
                    text = stringResource(R.string.normal_climate),
                    isSelected = state.climate == Climate.NORMAL,
                    onClick = { onAction(WaterIntakeEvent.UpdateClimate(Climate.NORMAL)) },
                    modifier = Modifier.weight(1f)
                )
                ClimateButton(
                    text = stringResource(R.string.hot_climate),
                    isSelected = state.climate == Climate.HOT,
                    onClick = { onAction(WaterIntakeEvent.UpdateClimate(Climate.HOT)) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ClimateButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val borderColor = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.3f)
    val backgroundColor = if (isSelected) primaryColor.copy(alpha = 0.2f) else Color.Transparent

    Button(
        onClick = onClick,
        modifier = modifier.border(1.dp, borderColor, RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = if (isSelected) primaryColor else CyberpunkTextSecondary
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        TechText(
            text = text.uppercase(),
            fontWeight = FontWeight.Bold,
            color = if (isSelected) primaryColor else CyberpunkTextSecondary
        )
    }
}

@Composable
private fun WaterIntakeResults(state: WaterIntakeState) {
    if (state.waterIntakeLiters > 0) {
        CyberpunkCard(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
            borderColor = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TechText(
                    text = stringResource(R.string.daily_water_intake).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                TechText(
                    text = "${String.format("%.1f", state.waterIntakeLiters)} L",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                TechText(
                    text = stringResource(R.string.cups_approx, state.waterIntakeCups),
                    color = CyberpunkTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tips
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    TechText(
                        text = stringResource(R.string.hydration_tips).uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    TechText(
                        text = "Based on activity level: ${state.activityLevel.name.replace("_", " ")}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp
                    )
                    TechText(
                        text = "• " + stringResource(R.string.hydration_tip_1),
                        color = CyberpunkTextSecondary,
                        fontSize = 12.sp
                    )
                    TechText(
                        text = "• " + stringResource(R.string.hydration_tip_2),
                        color = CyberpunkTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
