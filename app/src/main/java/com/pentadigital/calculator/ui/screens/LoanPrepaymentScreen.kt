package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.pentadigital.calculator.viewmodels.LoanPrepaymentEvent
import com.pentadigital.calculator.viewmodels.LoanPrepaymentState
import java.text.NumberFormat
import java.util.Locale
import android.content.Context
import android.content.Intent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

private fun sharePrepaymentResult(context: Context, state: LoanPrepaymentState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val subject = context.getString(R.string.prepayment_share_subject)
    val body = context.getString(
        R.string.prepayment_share_body,
        currencyFormat.format(state.loanAmount),
        currencyFormat.format(state.interestSaved),
        state.timeSavedMonths.toString()
    )
    
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    context.startActivity(Intent.createChooser(intent, "Share via"))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoanPrepaymentScreen(
    state: LoanPrepaymentState,
    onAction: (LoanPrepaymentEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    TechText(
                        stringResource(R.string.loan_prepayment_title).uppercase(),
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
                actions = {
                    IconButton(onClick = { com.pentadigital.calculator.utils.PdfGenerator.generateLoanPrepaymentPdf(context, state) }) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(R.drawable.ic_download),
                            contentDescription = stringResource(R.string.save_as_pdf),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { sharePrepaymentResult(context, state) }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
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
                // Left Pane: Inputs
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrepaymentInputs(state, onAction)
                }

                // Right Pane: Results
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrepaymentResults(state)
                    Spacer(modifier = Modifier.height(50.dp))
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
                PrepaymentInputs(state, onAction)
                PrepaymentResults(state)
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
private fun PrepaymentInputs(
    state: LoanPrepaymentState,
    onAction: (LoanPrepaymentEvent) -> Unit
) {
    val loanFocus = remember { FocusRequester() }
    val interestFocus = remember { FocusRequester() }
    val tenureFocus = remember { FocusRequester() }
    val monthlyPrepayFocus = remember { FocusRequester() }
    val lumpsumPrepayFocus = remember { FocusRequester() }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CyberpunkInput(
                value = if (state.loanAmount == 0.0) "" else if (state.loanAmount % 1.0 == 0.0) state.loanAmount.toInt().toString() else state.loanAmount.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(LoanPrepaymentEvent.UpdateLoanAmount(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(LoanPrepaymentEvent.UpdateLoanAmount(num))
                        }
                    }
                },
                label = stringResource(R.string.loan_amount).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(loanFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            CyberpunkInput(
                value = if (state.interestRate == 0.0) "" else if (state.interestRate % 1.0 == 0.0) state.interestRate.toInt().toString() else state.interestRate.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(LoanPrepaymentEvent.UpdateInterestRate(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(LoanPrepaymentEvent.UpdateInterestRate(num))
                        }
                    }
                },
                label = stringResource(R.string.interest_rate).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(interestFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            CyberpunkInput(
                value = if (state.tenureYears == 0) "" else state.tenureYears.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(LoanPrepaymentEvent.UpdateTenure(0))
                    } else {
                        it.toIntOrNull()?.let { num ->
                            onAction(LoanPrepaymentEvent.UpdateTenure(num))
                        }
                    }
                },
                label = stringResource(R.string.loan_tenure).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(tenureFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )
        }
    }

    TechText(
        text = "PREPAYMENT OPTIONS",
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.secondary
    )

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CyberpunkInput(
                value = if (state.monthlyPrepayment == 0.0) "" else if (state.monthlyPrepayment % 1.0 == 0.0) state.monthlyPrepayment.toInt().toString() else state.monthlyPrepayment.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(LoanPrepaymentEvent.UpdateMonthlyPrepayment(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(LoanPrepaymentEvent.UpdateMonthlyPrepayment(num))
                        }
                    }
                },
                label = stringResource(R.string.monthly_prepayment).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(monthlyPrepayFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            CyberpunkInput(
                value = if (state.lumpsumPrepayment == 0.0) "" else if (state.lumpsumPrepayment % 1.0 == 0.0) state.lumpsumPrepayment.toInt().toString() else state.lumpsumPrepayment.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(LoanPrepaymentEvent.UpdateLumpsumPrepayment(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(LoanPrepaymentEvent.UpdateLumpsumPrepayment(num))
                        }
                    }
                },
                label = stringResource(R.string.lumpsum_prepayment).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(lumpsumPrepayFocus),
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
private fun PrepaymentResults(state: LoanPrepaymentState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    // Savings Card
    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TechText(
                text = stringResource(R.string.total_savings).uppercase(),
                color = CyberpunkTextSecondary,
                fontSize = 14.sp
            )
            TechText(
                text = currencyFormat.format(state.interestSaved),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = androidx.compose.ui.res.painterResource(R.drawable.ic_launcher_foreground), // Placeholder or use a time icon if available
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                TechText(
                    text = "${state.timeSavedMonths} ${stringResource(R.string.months)} ${stringResource(R.string.time_saved)}".uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    // Comparison Details
    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResultRow(
                label = stringResource(R.string.original_interest).uppercase(),
                value = currencyFormat.format(state.originalTotalInterest),
                valueColor = CyberpunkTextSecondary
            )
            ResultRow(
                label = stringResource(R.string.new_interest).uppercase(),
                value = currencyFormat.format(state.newTotalInterest),
                valueColor = MaterialTheme.colorScheme.secondary,
                isBold = true
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ResultRow(
                label = stringResource(R.string.original_tenure).uppercase(),
                value = "${state.originalTenureMonths} ${stringResource(R.string.months)}".uppercase(),
                valueColor = CyberpunkTextSecondary
            )
            ResultRow(
                label = stringResource(R.string.new_tenure).uppercase(),
                value = "${state.newTenureMonths} ${stringResource(R.string.months)}".uppercase(),
                valueColor = MaterialTheme.colorScheme.secondary,
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
