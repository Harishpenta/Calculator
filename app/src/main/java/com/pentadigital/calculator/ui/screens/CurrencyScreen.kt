package com.pentadigital.calculator.ui.screens

import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.utils.*
import com.pentadigital.calculator.ui.theme.*
import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
fun CurrencyScreen(
    state: CurrencyState,
    onAction: (CurrencyEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val currencies = listOf("USD", "EUR", "INR", "GBP", "JPY")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    TechText(
                        stringResource(com.pentadigital.calculator.R.string.currency_title).uppercase(), 
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
                actions = {
                    val shareBodyTemplate = stringResource(com.pentadigital.calculator.R.string.currency_share_body)
                    val shareTitle = stringResource(com.pentadigital.calculator.R.string.currency_result_title)
                    val shareIconDesc = stringResource(com.pentadigital.calculator.R.string.share)

                    IconButton(onClick = {
                        val shareBody = String.format(
                            shareBodyTemplate,
                            "${state.amount}",
                            state.fromCurrency,
                            String.format("%.2f", state.convertedAmount),
                            state.toCurrency
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
                    // Amount Input
                    CyberpunkInput(
                        value = if (state.amount == 0.0) "" else if (state.amount % 1.0 == 0.0) state.amount.toInt().toString() else state.amount.toString(),
                        onValueChange = {
                            if (it.isEmpty()) {
                                onAction(CurrencyEvent.UpdateAmount(0.0))
                            } else {
                                it.toDoubleOrNull()?.let { num ->
                                    onAction(CurrencyEvent.UpdateAmount(num))
                                }
                            }
                        },
                        label = stringResource(com.pentadigital.calculator.R.string.amount).uppercase(),
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        )
                    )

                    // Currency Selection
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CurrencyDropdown(
                            selectedCurrency = state.fromCurrency,
                            currencies = currencies,
                            onCurrencySelected = { onAction(CurrencyEvent.UpdateFromCurrency(it)) }
                        )

                        IconButton(onClick = { onAction(CurrencyEvent.SwapCurrencies) }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(com.pentadigital.calculator.R.string.swap),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        CurrencyDropdown(
                            selectedCurrency = state.toCurrency,
                            currencies = currencies,
                            onCurrencySelected = { onAction(CurrencyEvent.UpdateToCurrency(it)) }
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
                    // Result Card
                    CyberpunkCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TechText(
                                text = "CONVERTED AMOUNT",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 16.sp
                            )
                            TechText(
                                text = "${String.format("%.2f", state.convertedAmount)} ${state.toCurrency}",
                                color = MaterialTheme.colorScheme.secondary,
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold
                            )
                            TechText(
                                text = stringResource(
                                    com.pentadigital.calculator.R.string.exchange_rate_display,
                                    state.fromCurrency,
                                    String.format("%.4f", state.convertedAmount / state.amount),
                                    state.toCurrency
                                ).uppercase(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                            
                            ConversionVisual(
                                fromLabel = state.fromCurrency,
                                toLabel = state.toCurrency,
                                accentColor = MaterialTheme.colorScheme.secondary
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
                // Amount Input
                CyberpunkInput(
                    value = if (state.amount == 0.0) "" else if (state.amount % 1.0 == 0.0) state.amount.toInt().toString() else state.amount.toString(),
                    onValueChange = {
                        if (it.isEmpty()) {
                            onAction(CurrencyEvent.UpdateAmount(0.0))
                        } else {
                            it.toDoubleOrNull()?.let { num ->
                                onAction(CurrencyEvent.UpdateAmount(num))
                            }
                        }
                    },
                    label = stringResource(com.pentadigital.calculator.R.string.amount).uppercase(),
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = androidx.compose.ui.text.input.ImeAction.Done
                    )
                )

                // Currency Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CurrencyDropdown(
                        selectedCurrency = state.fromCurrency,
                        currencies = currencies,
                        onCurrencySelected = { onAction(CurrencyEvent.UpdateFromCurrency(it)) }
                    )

                    IconButton(onClick = { onAction(CurrencyEvent.SwapCurrencies) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(com.pentadigital.calculator.R.string.swap),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    CurrencyDropdown(
                        selectedCurrency = state.toCurrency,
                        currencies = currencies,
                        onCurrencySelected = { onAction(CurrencyEvent.UpdateToCurrency(it)) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Result Card
                CyberpunkCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = MaterialTheme.colorScheme.secondary
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TechText(
                            text = "CONVERTED AMOUNT",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                        TechText(
                            text = "${String.format("%.2f", state.convertedAmount)} ${state.toCurrency}",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold
                        )
                        TechText(
                            text = stringResource(
                                com.pentadigital.calculator.R.string.exchange_rate_display,
                                state.fromCurrency,
                                String.format("%.4f", state.convertedAmount / state.amount),
                                state.toCurrency
                            ).uppercase(),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                        
                        ConversionVisual(
                            fromLabel = state.fromCurrency,
                            toLabel = state.toCurrency,
                            accentColor = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyDropdown(
    selectedCurrency: String,
    currencies: List<String>,
    onCurrencySelected: (String) -> Unit
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
                text = selectedCurrency,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
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
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = { TechText(text = currency, color = MaterialTheme.colorScheme.onSurface) },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
                )
            }
        }
    }
}
