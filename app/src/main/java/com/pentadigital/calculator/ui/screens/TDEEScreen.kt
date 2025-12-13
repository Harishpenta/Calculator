package com.pentadigital.calculator.ui.screens


import androidx.compose.foundation.clickable
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
import com.pentadigital.calculator.ui.components.CyberpunkCard
import com.pentadigital.calculator.ui.components.CyberpunkButton
import com.pentadigital.calculator.ui.components.TechText
import com.pentadigital.calculator.ui.components.GlowingDivider
import com.pentadigital.calculator.ui.components.CyberpunkWeightGauge
import com.pentadigital.calculator.ui.components.CyberpunkHeightRuler
import com.pentadigital.calculator.ui.theme.CyberpunkDarkBG
import com.pentadigital.calculator.ui.theme.CyberpunkSurface
import com.pentadigital.calculator.ui.theme.NeonCyan
import com.pentadigital.calculator.ui.theme.NeonPurple
import com.pentadigital.calculator.ui.theme.NeonGreen
import com.pentadigital.calculator.ui.theme.CyberpunkTextPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import com.pentadigital.calculator.ui.theme.CyberpunkTextSecondary
import com.pentadigital.calculator.utils.isLandscape
import com.pentadigital.calculator.viewmodels.ActivityLevel
import com.pentadigital.calculator.viewmodels.Gender
import com.pentadigital.calculator.viewmodels.TDEEEvent
import com.pentadigital.calculator.viewmodels.TDEEState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TDEEScreen(
    state: TDEEState,
    onAction: (TDEEEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { TechText(stringResource(com.pentadigital.calculator.R.string.tdee_title).uppercase(), color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(com.pentadigital.calculator.R.string.back), tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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
                    TDEEInputs(state, onAction)
                    CyberpunkButton(
                        onClick = { onAction(TDEEEvent.Calculate) },
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
                    TDEEResults(state)
                }
            }
        } else {
            val scrollState = rememberScrollState()

            LaunchedEffect(state.tdee) {
                if (state.tdee > 0) {
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
                TDEEInputs(state, onAction)
                CyberpunkButton(
                    onClick = { onAction(TDEEEvent.Calculate) },
                    text = stringResource(R.string.calculate),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                TDEEResults(state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TDEEInputs(
    state: TDEEState,
    onAction: (TDEEEvent) -> Unit
) {
    CyberpunkCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = MaterialTheme.colorScheme.primary
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Gender Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GenderButton(
                    text = stringResource(R.string.male),
                    isSelected = state.gender == Gender.MALE,
                    onClick = { onAction(TDEEEvent.UpdateGender(Gender.MALE)) },
                    modifier = Modifier.weight(1f)
                )
                GenderButton(
                    text = stringResource(R.string.female),
                    isSelected = state.gender == Gender.FEMALE,
                    onClick = { onAction(TDEEEvent.UpdateGender(Gender.FEMALE)) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Age
            OutlinedTextField(
                value = state.userAge,
                onValueChange = { newValue -> if (newValue.all { char -> char.isDigit() }) onAction(TDEEEvent.UpdateAgeValue(newValue)) },
                label = { TechText(stringResource(R.string.age), color = CyberpunkTextSecondary) },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = CyberpunkTextSecondary,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Weight & Height
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Weight
                TechText(
                    text = stringResource(R.string.weight_kg).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                CyberpunkWeightGauge(
                    value = state.weight.toFloatOrNull() ?: 70f,
                    onValueChange = { onAction(TDEEEvent.UpdateWeight(String.format("%.1f", it))) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                )

                // Height
                TechText(
                    text = stringResource(R.string.height_cm).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                CyberpunkHeightRuler(
                    value = state.height.toFloatOrNull() ?: 170f,
                    onValueChange = { onAction(TDEEEvent.UpdateHeight(String.format("%.1f", it))) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                        .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Activity Level
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = state.activityLevel.label,
                    onValueChange = {},
                    readOnly = true,
                    label = { TechText(stringResource(R.string.activity_level), color = CyberpunkTextSecondary) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = CyberpunkTextSecondary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    )
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.primary)
                ) {
                    ActivityLevel.values().forEach { level ->
                        DropdownMenuItem(
                            text = { TechText(level.label, color = MaterialTheme.colorScheme.onSurface) },
                            onClick = {
                                onAction(TDEEEvent.UpdateActivityLevel(level))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenderButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val borderColor = if (isSelected) primaryColor else primaryColor.copy(alpha = 0.3f)
    val backgroundColor = if (isSelected) primaryColor.copy(alpha = 0.1f) else Color.Transparent
    
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
private fun TDEEResults(state: TDEEState) {
    if (state.tdee > 0) {
        CyberpunkCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = MaterialTheme.colorScheme.secondary
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TechText(
                    text = stringResource(R.string.maintenance_calories).uppercase(),
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                TechText(
                    text = "${state.maintenance} KCAL",
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                TechText(
                    text = "PER DAY",
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        CyberpunkCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = MaterialTheme.colorScheme.primary
        ) {
            Column(
                modifier = Modifier.padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultRow(stringResource(R.string.bmr), "${state.bmr} KCAL")
                GlowingDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ResultRow(stringResource(R.string.cutting), "${state.cutting} KCAL", MaterialTheme.colorScheme.primary)
                GlowingDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                ResultRow(stringResource(R.string.bulking), "${state.bulking} KCAL", MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
private fun ResultRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TechText(
            text = label.uppercase(),
            color = CyberpunkTextSecondary,
            fontSize = 14.sp
        )
        TechText(
            text = value,
            color = valueColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
