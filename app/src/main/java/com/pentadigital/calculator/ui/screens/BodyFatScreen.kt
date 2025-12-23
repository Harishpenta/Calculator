package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.size
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.theme.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.utils.isLandscape
import com.pentadigital.calculator.viewmodels.BodyFatEvent
import com.pentadigital.calculator.viewmodels.BodyFatState
import com.pentadigital.calculator.viewmodels.Gender

// Define local neon red
private val NeonRed = Color(0xFFFF5252)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BodyFatScreen(
    state: BodyFatState,
    onAction: (BodyFatEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.body_fat_title).uppercase(),
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
                    BodyFatInputs(state, onAction)
                    CyberpunkButton(
                        onClick = { onAction(BodyFatEvent.Calculate) },
                        text = stringResource(R.string.calculate),
                        modifier = Modifier.fillMaxWidth(),
                        color = NeonRed
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    BodyFatResults(state)
                }
            }
        } else {
            val scrollState = rememberScrollState()
            
            LaunchedEffect(state.bodyFatPercentage) {
                if (state.bodyFatPercentage > 0) {
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
                BodyFatInputs(state, onAction)
                CyberpunkButton(
                    onClick = { onAction(BodyFatEvent.Calculate) },
                    text = stringResource(R.string.calculate),
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
                BodyFatResults(state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BodyFatInputs(
    state: BodyFatState,
    onAction: (BodyFatEvent) -> Unit
) {
    CyberpunkCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = NeonPurple
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Gender Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                GenderButton(
                    text = stringResource(R.string.male),
                    isSelected = state.gender == Gender.MALE,
                    onClick = { onAction(BodyFatEvent.UpdateGender(Gender.MALE)) },
                    modifier = Modifier.weight(1f)
                )
                GenderButton(
                    text = stringResource(R.string.female),
                    isSelected = state.gender == Gender.FEMALE,
                    onClick = { onAction(BodyFatEvent.UpdateGender(Gender.FEMALE)) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Age
            OutlinedTextField(
                value = state.age,
                onValueChange = { if (it.all { char -> char.isDigit() }) onAction(BodyFatEvent.UpdateAge(it)) },
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
                    onValueChange = { onAction(BodyFatEvent.UpdateWeight(String.format("%.1f", it))) },
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
                    onValueChange = { onAction(BodyFatEvent.UpdateHeight(String.format("%.1f", it))) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                        .border(1.dp, NeonGreen.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                )
            }

            // Neck & Waist
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Neck
                TechText(
                    text = stringResource(R.string.neck_cm).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                CyberpunkWeightGauge(
                    value = state.neck.toFloatOrNull() ?: 40f,
                    onValueChange = { onAction(BodyFatEvent.UpdateNeck(String.format("%.1f", it))) },
                    range = 20f..80f,
                    unit = "CM",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                        .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                )

                // Waist
                TechText(
                    text = stringResource(R.string.waist_cm).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                CyberpunkWeightGauge(
                    value = state.waist.toFloatOrNull() ?: 80f,
                    onValueChange = { onAction(BodyFatEvent.UpdateWaist(String.format("%.1f", it))) },
                    range = 40f..150f,
                    unit = "CM",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                        .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                )
            }

            // Hip (Only for Female)
            if (state.gender == Gender.FEMALE) {
                TechText(
                    text = stringResource(R.string.hip_cm).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                CyberpunkWeightGauge(
                    value = state.hip.toFloatOrNull() ?: 90f,
                    onValueChange = { onAction(BodyFatEvent.UpdateHip(String.format("%.1f", it))) },
                    range = 40f..150f,
                    unit = "CM",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
                        .border(1.dp, NeonPurple.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                )
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
private fun BodyFatResults(state: BodyFatState) {
    if (state.bodyFatPercentage > 0) {
        CyberpunkCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonRed
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TechText(
                    text = stringResource(R.string.body_fat_percentage).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                TechText(
                    text = String.format("%.1f%%", state.bodyFatPercentage),
                    color = NeonRed,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                TechText(
                    text = state.bodyFatCategory.uppercase(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Result Bar for Body Fat (0-50%)
                CyberpunkResultBar(
                    modifier = Modifier.fillMaxWidth(),
                    bmi = state.bodyFatPercentage.toFloat(), // Reusing BMI bar logic but mapping value
                    category = "" // Category already shown above
                )
                
                BodyFatLegend(modifier = Modifier.padding(top = 16.dp))
            }
        }

    }
}

@Composable
private fun BodyFatLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LegendItem(color = Color(0xFF00E5FF), label = stringResource(R.string.essential_fat_label))
        LegendItem(color = NeonGreen, label = stringResource(R.string.fitness_label))
        LegendItem(color = Color(0xFFFFB74D), label = stringResource(R.string.average_label))
        LegendItem(color = NeonRed, label = stringResource(R.string.obese_label))
    }
}

@Composable
private fun LegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(color)
        )
        TechText(
            text = label, 
            fontSize = 10.sp, 
            color = CyberpunkTextSecondary,
            fontWeight = FontWeight.Bold
        )
    }
}
