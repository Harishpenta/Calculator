package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.pentadigital.calculator.viewmodels.UnitPriceEvent
import com.pentadigital.calculator.viewmodels.UnitPriceState
import java.text.NumberFormat
import java.util.Locale

import androidx.compose.foundation.layout.WindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitPriceScreen(
    state: UnitPriceState,
    onAction: (UnitPriceEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        text = stringResource(R.string.unit_price_comparator_title).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
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
                    UnitPriceInputs(state, onAction)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    UnitPriceResults(state)
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
                UnitPriceInputs(state, onAction)
                UnitPriceResults(state)
            }
        }
    }
}

@Composable
private fun UnitPriceInputs(
    state: UnitPriceState,
    onAction: (UnitPriceEvent) -> Unit
) {
    val priceAFocus = remember { FocusRequester() }
    val qtyAFocus = remember { FocusRequester() }
    val priceBFocus = remember { FocusRequester() }
    val qtyBFocus = remember { FocusRequester() }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Item A
        CyberpunkCard(
            borderColor = NeonPurple,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TechText(
                    text = stringResource(R.string.item_a).uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                CyberpunkInput(
                    value = if (state.priceA == 0.0) "" else if (state.priceA % 1.0 == 0.0) state.priceA.toInt().toString() else state.priceA.toString(),
                    onValueChange = {
                        if (it.isEmpty()) onAction(UnitPriceEvent.UpdatePriceA(0.0))
                        else it.toDoubleOrNull()?.let { num -> onAction(UnitPriceEvent.UpdatePriceA(num)) }
                    },
                    label = stringResource(R.string.price).uppercase(),
                    modifier = Modifier.fillMaxWidth().focusRequester(priceAFocus),
                    borderColor = MaterialTheme.colorScheme.primary,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                CyberpunkInput(
                    value = if (state.quantityA == 0.0) "" else if (state.quantityA % 1.0 == 0.0) state.quantityA.toInt().toString() else state.quantityA.toString(),
                    onValueChange = {
                        if (it.isEmpty()) onAction(UnitPriceEvent.UpdateQuantityA(0.0))
                        else it.toDoubleOrNull()?.let { num -> onAction(UnitPriceEvent.UpdateQuantityA(num)) }
                    },
                    label = stringResource(R.string.quantity).uppercase(),
                    modifier = Modifier.fillMaxWidth().focusRequester(qtyAFocus),
                    borderColor = MaterialTheme.colorScheme.primary,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
            }
        }

        // Item B
        CyberpunkCard(
            borderColor = NeonPurple,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TechText(
                    text = stringResource(R.string.item_b).uppercase(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                CyberpunkInput(
                    value = if (state.priceB == 0.0) "" else if (state.priceB % 1.0 == 0.0) state.priceB.toInt().toString() else state.priceB.toString(),
                    onValueChange = {
                        if (it.isEmpty()) onAction(UnitPriceEvent.UpdatePriceB(0.0))
                        else it.toDoubleOrNull()?.let { num -> onAction(UnitPriceEvent.UpdatePriceB(num)) }
                    },
                    label = stringResource(R.string.price).uppercase(),
                    modifier = Modifier.fillMaxWidth().focusRequester(priceBFocus),
                    borderColor = MaterialTheme.colorScheme.primary,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    )
                )
                CyberpunkInput(
                    value = if (state.quantityB == 0.0) "" else if (state.quantityB % 1.0 == 0.0) state.quantityB.toInt().toString() else state.quantityB.toString(),
                    onValueChange = {
                        if (it.isEmpty()) onAction(UnitPriceEvent.UpdateQuantityB(0.0))
                        else it.toDoubleOrNull()?.let { num -> onAction(UnitPriceEvent.UpdateQuantityB(num)) }
                    },
                    label = stringResource(R.string.quantity).uppercase(),
                    modifier = Modifier.fillMaxWidth().focusRequester(qtyBFocus),
                    borderColor = MaterialTheme.colorScheme.primary,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
    }
}

@Composable
private fun UnitPriceResults(state: UnitPriceState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    if (state.verdict.isNotEmpty()) {
        CyberpunkCard(
            borderColor = NeonGreen,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TechText(
                    text = state.verdict.uppercase(),
                    color = NeonGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                if (state.savings > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    TechText(
                        text = "${stringResource(R.string.savings).uppercase()}: ${String.format("%.1f", state.savings)}%",
                        color = NeonGreen.copy(alpha = 0.8f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }

    CyberpunkCard(
        borderColor = NeonPurple,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResultRow(
                label = "${stringResource(R.string.item_a)} (${stringResource(R.string.price_per_unit)})".uppercase(),
                value = currencyFormat.format(state.unitPriceA),
                valueColor = if (state.isItemACheaper) NeonGreen else MaterialTheme.colorScheme.onSurface,
                isBold = state.isItemACheaper
            )
            ResultRow(
                label = "${stringResource(R.string.item_b)} (${stringResource(R.string.price_per_unit)})".uppercase(),
                value = currencyFormat.format(state.unitPriceB),
                valueColor = if (state.isItemBCheaper) NeonGreen else MaterialTheme.colorScheme.onSurface,
                isBold = state.isItemBCheaper
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
