package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.pentadigital.calculator.viewmodels.DiscountEvent
import com.pentadigital.calculator.viewmodels.DiscountState
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscountScreen(
    state: DiscountState,
    onAction: (DiscountEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.discount_calculator_title).uppercase(),
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
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
                    DiscountInputs(state, onAction)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DiscountResults(state)
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
                DiscountInputs(state, onAction)
                DiscountResults(state)
            }
        }
    }
}

@Composable
private fun DiscountInputs(
    state: DiscountState,
    onAction: (DiscountEvent) -> Unit
) {
    val originalPriceFocus = remember { FocusRequester() }
    val discountFocus = remember { FocusRequester() }
    val taxFocus = remember { FocusRequester() }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CyberpunkInput(
                value = if (state.originalPrice == 0.0) "" else if (state.originalPrice % 1.0 == 0.0) state.originalPrice.toInt().toString() else state.originalPrice.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(DiscountEvent.UpdateOriginalPrice(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(DiscountEvent.UpdateOriginalPrice(num))
                        }
                    }
                },
                label = stringResource(R.string.original_price).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(originalPriceFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            CyberpunkInput(
                value = if (state.discountPercentage == 0.0) "" else if (state.discountPercentage % 1.0 == 0.0) state.discountPercentage.toInt().toString() else state.discountPercentage.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(DiscountEvent.UpdateDiscount(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(DiscountEvent.UpdateDiscount(num))
                        }
                    }
                },
                label = stringResource(R.string.discount_percentage).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(discountFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            CyberpunkInput(
                value = if (state.taxPercentage == 0.0) "" else if (state.taxPercentage % 1.0 == 0.0) state.taxPercentage.toInt().toString() else state.taxPercentage.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(DiscountEvent.UpdateTax(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(DiscountEvent.UpdateTax(num))
                        }
                    }
                },
                label = stringResource(R.string.tax_percentage).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(taxFocus),
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
private fun DiscountResults(state: DiscountState) {
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
                text = stringResource(R.string.final_price).uppercase(),
                color = CyberpunkTextSecondary,
                fontSize = 14.sp
            )
            TechText(
                text = currencyFormat.format(state.finalPrice),
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
                label = stringResource(R.string.you_save).uppercase(),
                value = currencyFormat.format(state.amountSaved),
                valueColor = MaterialTheme.colorScheme.secondary,
                isBold = true
            )
            ResultRow(
                label = stringResource(R.string.tax_amount).uppercase(),
                value = currencyFormat.format(state.taxAmount),
                valueColor = CyberpunkTextSecondary
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
