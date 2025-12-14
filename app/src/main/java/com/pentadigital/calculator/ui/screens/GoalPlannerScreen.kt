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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import com.pentadigital.calculator.viewmodels.GoalPlannerEvent
import com.pentadigital.calculator.viewmodels.GoalPlannerState
import java.text.NumberFormat
import java.util.Locale
import android.content.Context
import android.content.Intent
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.foundation.layout.WindowInsets

private fun shareGoalResult(context: Context, state: GoalPlannerState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    val subject = context.getString(R.string.goal_share_subject)
    val body = context.getString(
        R.string.goal_share_body,
        currencyFormat.format(state.targetAmount.toDoubleOrNull() ?: 0.0),
        currencyFormat.format(state.requiredMonthlyInvestment),
        state.timePeriodYears
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
fun GoalPlannerScreen(
    state: GoalPlannerState,
    onAction: (GoalPlannerEvent) -> Unit,
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
                        stringResource(R.string.goal_planner_title).uppercase(),
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
                    IconButton(onClick = { com.pentadigital.calculator.utils.PdfGenerator.generateGoalPlannerPdf(context, state) }) {
                        Icon(
                            painter = androidx.compose.ui.res.painterResource(R.drawable.ic_download),
                            contentDescription = stringResource(R.string.save_as_pdf),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = { shareGoalResult(context, state) }) {
                        Icon(
                            Icons.Default.Share,
                            contentDescription = stringResource(R.string.share),
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
                // Left Pane: Inputs
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GoalInputs(state, onAction)
                }

                // Right Pane: Results
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    GoalResults(state)
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
                GoalInputs(state, onAction)
                GoalResults(state)
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}

@Composable
private fun GoalInputs(
    state: GoalPlannerState,
    onAction: (GoalPlannerEvent) -> Unit
) {
    val targetFocus = remember { FocusRequester() }
    val timeFocus = remember { FocusRequester() }
    val rateFocus = remember { FocusRequester() }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CyberpunkInput(
                value = state.targetAmount,
                onValueChange = { onAction(GoalPlannerEvent.UpdateTargetAmount(it)) },
                label = stringResource(R.string.target_amount).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(targetFocus),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                borderColor = MaterialTheme.colorScheme.primary
            )

            CyberpunkInput(
                value = state.timePeriodYears,
                onValueChange = { onAction(GoalPlannerEvent.UpdateTimePeriod(it)) },
                label = stringResource(R.string.time_period).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(timeFocus),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                borderColor = MaterialTheme.colorScheme.primary
            )

            CyberpunkInput(
                value = state.expectedReturnRate,
                onValueChange = { onAction(GoalPlannerEvent.UpdateReturnRate(it)) },
                label = stringResource(R.string.expected_return_rate).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(rateFocus),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                borderColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun GoalResults(state: GoalPlannerState) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

    // Required Monthly Investment Card
    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TechText(
                text = stringResource(R.string.required_monthly_investment).uppercase(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            TechText(
                text = currencyFormat.format(state.requiredMonthlyInvestment),
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    // Chart and Details
    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            PieChart(
                data = listOf(
                    PieChartData(state.totalInvestment, CyberpunkTextSecondary, stringResource(R.string.total_investment)),
                    PieChartData(state.totalReturns, MaterialTheme.colorScheme.secondary, stringResource(R.string.total_returns))
                ),
                chartSize = 160.dp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ResultRow(
                    label = stringResource(R.string.total_investment).uppercase(),
                    value = currencyFormat.format(state.totalInvestment),
                    valueColor = MaterialTheme.colorScheme.onSurface
                )
                ResultRow(
                    label = stringResource(R.string.total_returns).uppercase(),
                    value = currencyFormat.format(state.totalReturns),
                    valueColor = MaterialTheme.colorScheme.secondary,
                    isBold = true
                )
            }
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
