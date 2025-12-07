package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.ui.theme.*
import com.pentadigital.calculator.utils.isLandscape
import com.pentadigital.calculator.viewmodels.FuelCostEvent
import com.pentadigital.calculator.viewmodels.FuelCostState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FuelCostScreen(
    state: FuelCostState,
    onAction: (FuelCostEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.fuel_cost_calculator_title).uppercase(),
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
                    FuelCostInputs(state, onAction)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FuelCostResults(state)
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
                FuelCostInputs(state, onAction)
                FuelCostResults(state)
            }
        }
    }
}

@Composable
private fun FuelCostInputs(
    state: FuelCostState,
    onAction: (FuelCostEvent) -> Unit
) {
    val distanceFocus = remember { FocusRequester() }
    val mileageFocus = remember { FocusRequester() }
    val priceFocus = remember { FocusRequester() }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CyberpunkInput(
                value = if (state.distance == 0.0) "" else if (state.distance % 1.0 == 0.0) state.distance.toInt().toString() else state.distance.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(FuelCostEvent.UpdateDistance(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(FuelCostEvent.UpdateDistance(num))
                        }
                    }
                },
                label = stringResource(R.string.distance).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(distanceFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            CyberpunkInput(
                value = if (state.mileage == 0.0) "" else if (state.mileage % 1.0 == 0.0) state.mileage.toInt().toString() else state.mileage.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(FuelCostEvent.UpdateMileage(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(FuelCostEvent.UpdateMileage(num))
                        }
                    }
                },
                label = stringResource(R.string.mileage).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(mileageFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            CyberpunkInput(
                value = if (state.fuelPrice == 0.0) "" else if (state.fuelPrice % 1.0 == 0.0) state.fuelPrice.toInt().toString() else state.fuelPrice.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(FuelCostEvent.UpdateFuelPrice(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(FuelCostEvent.UpdateFuelPrice(num))
                        }
                    }
                },
                label = stringResource(R.string.fuel_price).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(priceFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )
        }
    }
}

@Composable
private fun FuelCostResults(state: FuelCostState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val numberFormat = NumberFormat.getInstance(Locale("en", "IN")).apply {
        maximumFractionDigits = 2
    }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TechText(
                text = stringResource(R.string.total_cost).uppercase(),
                color = CyberpunkTextSecondary,
                fontSize = 14.sp
            )
            TechText(
                text = currencyFormat.format(state.totalCost),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 32.sp,
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
                label = stringResource(R.string.fuel_required).uppercase(),
                value = "${numberFormat.format(state.fuelRequired)} L",
                valueColor = MaterialTheme.colorScheme.primary,
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
