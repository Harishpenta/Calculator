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

import androidx.compose.foundation.layout.WindowInsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SipScreen(
    state: SipState,
    onAction: (SipEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    TechText(
                        stringResource(com.pentadigital.calculator.R.string.sip_title).uppercase(), 
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
                    val shareBodyTemplate = stringResource(com.pentadigital.calculator.R.string.sip_share_body)
                    val shareTitle = stringResource(com.pentadigital.calculator.R.string.sip_result_title)
                    val shareIconDesc = stringResource(com.pentadigital.calculator.R.string.share)
                    
                    IconButton(onClick = {
                        val shareBody = String.format(
                            shareBodyTemplate,
                            "₹${state.monthlyInvestment}",
                            "${state.expectedReturnRate}",
                            "${state.timePeriodYears}",
                            "₹${String.format("%.2f", state.investedAmount)}",
                            "₹${String.format("%.2f", state.estimatedReturns)}",
                            "₹${String.format("%.2f", state.totalValue)}"
                        )
                        shareResult(context, shareTitle, shareBody)
                    }) {
                        Icon(Icons.Default.Share, contentDescription = shareIconDesc, tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                windowInsets = WindowInsets(0.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp)
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
                    SipInputs(state, onAction)
                }

                // Right Pane: Result
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SipResults(state)
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
                SipInputs(state, onAction)
                Spacer(modifier = Modifier.height(16.dp))
                SipResults(state)
            }
        }
    }
}

@Composable
private fun SipInputs(
    state: SipState,
    onAction: (SipEvent) -> Unit
) {
    val investmentFocus = remember { FocusRequester() }
    val returnFocus = remember { FocusRequester() }
    val timeFocus = remember { FocusRequester() }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Monthly Investment
            CyberpunkInput(
                value = state.monthlyInvestment,
                onValueChange = { onAction(SipEvent.UpdateInvestment(it)) },
                label = stringResource(com.pentadigital.calculator.R.string.monthly_investment).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(investmentFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            // Expected Return Rate
            CyberpunkInput(
                value = state.expectedReturnRate,
                onValueChange = { onAction(SipEvent.UpdateReturnRate(it)) },
                label = stringResource(com.pentadigital.calculator.R.string.expected_return_rate).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(returnFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            // Time Period
            CyberpunkInput(
                value = state.timePeriodYears,
                onValueChange = { onAction(SipEvent.UpdateTimePeriod(it)) },
                label = stringResource(com.pentadigital.calculator.R.string.time_period).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(timeFocus),
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
private fun SipResults(state: SipState) {
    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ResultRow(stringResource(com.pentadigital.calculator.R.string.invested_amount).uppercase(), state.investedAmount)
            ResultRow(stringResource(com.pentadigital.calculator.R.string.est_returns).uppercase(), state.estimatedReturns)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            PieChart(
                data = listOf(
                    PieChartData(state.investedAmount, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), stringResource(com.pentadigital.calculator.R.string.invested).uppercase()),
                    PieChartData(state.estimatedReturns, MaterialTheme.colorScheme.secondary, stringResource(com.pentadigital.calculator.R.string.returns).uppercase())
                ),
                chartSize = 200.dp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TechText(
                    text = stringResource(com.pentadigital.calculator.R.string.total_value).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                TechText(
                    text = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(state.totalValue),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, amount: Double) {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val formattedAmount = format.format(amount)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TechText(
            text = label,
            color = CyberpunkTextSecondary,
            fontSize = 14.sp
        )
        TechText(
            text = formattedAmount,
            color = CyberpunkTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
