package com.pentadigital.calculator.ui.screens

import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.utils.*
import com.pentadigital.calculator.ui.theme.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.ui.theme.NeonGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiScreen(
    state: BmiState,
    onAction: (BmiEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { TechText(stringResource(com.pentadigital.calculator.R.string.bmi_title).uppercase(), color = MaterialTheme.colorScheme.primary, fontSize = 20.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(Icons.Default.ArrowBack, contentDescription = stringResource(com.pentadigital.calculator.R.string.back), tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    val shareBodyTemplate = stringResource(com.pentadigital.calculator.R.string.bmi_share_body)
                    val shareTitle = stringResource(com.pentadigital.calculator.R.string.bmi_result_title)
                    val shareIconDesc = stringResource(com.pentadigital.calculator.R.string.share)
                    val categoryLabels = mapOf(
                        "Underweight" to stringResource(com.pentadigital.calculator.R.string.underweight),
                        "Normal" to stringResource(com.pentadigital.calculator.R.string.normal),
                        "Overweight" to stringResource(com.pentadigital.calculator.R.string.overweight),
                        "Obese" to stringResource(com.pentadigital.calculator.R.string.obese)
                    )

                    IconButton(onClick = {
                        val categoryLabel = if (state.category.isNotEmpty()) categoryLabels[state.category] ?: "" else ""
                        val shareBody = String.format(
                            shareBodyTemplate,
                            "${state.weightKg}",
                            "${state.heightCm}",
                            String.format("%.1f", state.bmi),
                            categoryLabel
                        )
                        shareResult(context, shareTitle, shareBody)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = shareIconDesc, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        val isLandscape = isLandscape()

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
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
                    // Weight
                    TechText(
                        text = stringResource(com.pentadigital.calculator.R.string.weight_kg).uppercase(),
                        color = CyberpunkTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    CyberpunkWeightGauge(
                        value = state.weightKg.toFloat(),
                        onValueChange = { onAction(BmiEvent.UpdateWeight(it.toDouble())) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    )

                    // Height
                    TechText(
                        text = stringResource(com.pentadigital.calculator.R.string.height_cm).uppercase(),
                        color = CyberpunkTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    CyberpunkHeightRuler(
                        value = state.heightCm.toFloat(),
                        onValueChange = { onAction(BmiEvent.UpdateHeight(it.toDouble())) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                            .border(1.dp, NeonGreen.copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    )
                }

                // Right Pane: Result
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Result Card
                    CyberpunkCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (state.category.isNotEmpty()) getColorForCategory(state.category) else MaterialTheme.colorScheme.primary
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            TechText(
                                text = stringResource(com.pentadigital.calculator.R.string.your_bmi).uppercase(),
                                color = CyberpunkTextSecondary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            TechText(
                                text = if (state.bmi > 0) String.format("%.1f", state.bmi) else "--.-",
                                color = if (state.category.isNotEmpty()) getColorForCategory(state.category) else MaterialTheme.colorScheme.primary,
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            CyberpunkResultBar(
                                modifier = Modifier.fillMaxWidth(),
                                bmi = state.bmi.toFloat(),
                                category = if (state.category.isNotEmpty()) stringResource(getLabelForCategory(state.category)) else ""
                            )
                        }
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Weight
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TechText(
                        text = stringResource(com.pentadigital.calculator.R.string.weight_kg).uppercase(),
                        color = CyberpunkTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    CyberpunkWeightGauge(
                        value = state.weightKg.toFloat(),
                        onValueChange = { onAction(BmiEvent.UpdateWeight(it.toDouble())) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    )
                }

                // Height
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TechText(
                        text = stringResource(com.pentadigital.calculator.R.string.height_cm).uppercase(),
                        color = CyberpunkTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CyberpunkHeightRuler(
                        value = state.heightCm.toFloat(),
                        onValueChange = { onAction(BmiEvent.UpdateHeight(it.toDouble())) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(250.dp)
                            .background(MaterialTheme.colorScheme.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                            .border(1.dp, NeonGreen.copy(alpha = 0.3f), androidx.compose.foundation.shape.RoundedCornerShape(16.dp))
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Result Card
                CyberpunkCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = if (state.category.isNotEmpty()) getColorForCategory(state.category) else MaterialTheme.colorScheme.primary
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TechText(
                            text = stringResource(com.pentadigital.calculator.R.string.your_bmi).uppercase(),
                            color = CyberpunkTextSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        TechText(
                            text = if (state.bmi > 0) String.format("%.1f", state.bmi) else "--.-",
                            color = if (state.category.isNotEmpty()) getColorForCategory(state.category) else MaterialTheme.colorScheme.primary,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Bold
                        )
                        
                        CyberpunkResultBar(
                            modifier = Modifier.fillMaxWidth(),
                            bmi = state.bmi.toFloat(),
                            category = if (state.category.isNotEmpty()) stringResource(getLabelForCategory(state.category)) else ""
                        )
                    }
                }
            }
        }
    }
}

fun getColorForCategory(category: String): Color {
    return when (category) {
        "Underweight" -> Color(0xFF00E5FF) // Light Blue Cyan
        "Normal" -> NeonGreen // Green
        "Overweight" -> Color(0xFFFFB74D) // Orange
        "Obese" -> Color(0xFFFF5252) // Red
        else -> Color.White
    }
}

@Composable
fun getLabelForCategory(category: String): Int {
    return when (category) {
        "Underweight" -> com.pentadigital.calculator.R.string.underweight
        "Normal" -> com.pentadigital.calculator.R.string.normal
        "Overweight" -> com.pentadigital.calculator.R.string.overweight
        "Obese" -> com.pentadigital.calculator.R.string.obese
        else -> com.pentadigital.calculator.R.string.error
    }
}
