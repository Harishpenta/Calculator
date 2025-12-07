package com.pentadigital.calculator.ui.screens

import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.utils.*
import com.pentadigital.calculator.ui.theme.*
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmiScreen(
    state: EmiState,
    onAction: (EmiEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(com.pentadigital.calculator.R.string.emi_title).uppercase(),
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
                    val shareBodyTemplate = stringResource(com.pentadigital.calculator.R.string.emi_share_body)
                    val shareTitle = stringResource(com.pentadigital.calculator.R.string.emi_result_title)
                    val shareIconDesc = stringResource(com.pentadigital.calculator.R.string.share)

                    IconButton(onClick = {
                        val shareBody = String.format(
                            shareBodyTemplate,
                            "₹${state.loanAmount}",
                            "${state.interestRate}",
                            "${state.tenureYears}",
                            "₹${String.format("%.2f", state.emi)}",
                            "₹${String.format("%.2f", state.totalInterest)}",
                            "₹${String.format("%.2f", state.totalPayment)}"
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
                    EmiInputs(state, onAction)
                }

                // Right Pane: Result
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EmiResults(state)
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
                EmiInputs(state, onAction)
                Spacer(modifier = Modifier.height(16.dp))
                EmiResults(state)
            }
        }
    }
}

@Composable
private fun EmiInputs(
    state: EmiState,
    onAction: (EmiEvent) -> Unit
) {
    val loanFocus = remember { FocusRequester() }
    val interestFocus = remember { FocusRequester() }
    val tenureFocus = remember { FocusRequester() }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Loan Amount
            CyberpunkInput(
                value = if (state.loanAmount == 0.0) "" else if (state.loanAmount % 1.0 == 0.0) state.loanAmount.toInt().toString() else state.loanAmount.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(EmiEvent.UpdateLoanAmount(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(EmiEvent.UpdateLoanAmount(num))
                        }
                    }
                },
                label = stringResource(com.pentadigital.calculator.R.string.loan_amount).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(loanFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            // Interest Rate
            CyberpunkInput(
                value = if (state.interestRate == 0.0) "" else if (state.interestRate % 1.0 == 0.0) state.interestRate.toInt().toString() else state.interestRate.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(EmiEvent.UpdateInterestRate(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(EmiEvent.UpdateInterestRate(num))
                        }
                    }
                },
                label = stringResource(com.pentadigital.calculator.R.string.interest_rate).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(interestFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            // Tenure
            CyberpunkInput(
                value = if (state.tenureYears == 0) "" else state.tenureYears.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(EmiEvent.UpdateTenure(0))
                    } else {
                        it.toIntOrNull()?.let { num ->
                            onAction(EmiEvent.UpdateTenure(num))
                        }
                    }
                },
                label = stringResource(com.pentadigital.calculator.R.string.loan_tenure).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(tenureFocus),
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
private fun EmiResults(state: EmiState) {
    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TechText(
                    text = stringResource(com.pentadigital.calculator.R.string.monthly_emi).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                TechText(
                    text = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(state.emi),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PieChart(
                    data = listOf(
                        PieChartData(state.loanAmount, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), stringResource(com.pentadigital.calculator.R.string.principal).uppercase()),
                        PieChartData(state.totalInterest, MaterialTheme.colorScheme.secondary, stringResource(com.pentadigital.calculator.R.string.interest).uppercase())
                    ),
                    chartSize = 160.dp
                )
                Spacer(modifier = Modifier.height(8.dp))
                ResultRow(stringResource(com.pentadigital.calculator.R.string.total_interest).uppercase(), state.totalInterest)
                ResultRow(stringResource(com.pentadigital.calculator.R.string.total_payment).uppercase(), state.totalPayment, isTotal = true)
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, amount: Double, isTotal: Boolean = false) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val formattedAmount = format.format(amount)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TechText(
            text = label,
            color = CyberpunkTextSecondary,
            fontSize = if (isTotal) 16.sp else 14.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Medium
        )
        TechText(
            text = formattedAmount,
            color = if (isTotal) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface,
            fontSize = if (isTotal) 18.sp else 16.sp,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.SemiBold
        )
    }
}
