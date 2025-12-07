package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.pentadigital.calculator.utils.shareResult
import com.pentadigital.calculator.viewmodels.CompoundInterestEvent
import com.pentadigital.calculator.viewmodels.CompoundInterestState
import com.pentadigital.calculator.viewmodels.CompoundingFrequency
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompoundInterestScreen(
    state: CompoundInterestState,
    onAction: (CompoundInterestEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.compound_interest_title).uppercase(),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
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
                    val shareBodyTemplate = stringResource(R.string.ci_share_body)
                    val shareTitle = stringResource(R.string.ci_result_title)
                    val shareIconDesc = stringResource(R.string.share)

                    IconButton(onClick = {
                        val shareBody = String.format(
                            shareBodyTemplate,
                            "₹${state.principal}",
                            "${state.rate}",
                            "${state.timeYears}",
                            state.frequency.name,
                            "₹${String.format("%.2f", state.interest)}",
                            "₹${String.format("%.2f", state.totalAmount)}"
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
                    CompoundInterestInputs(state, onAction)
                }

                // Right Pane: Result
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CompoundInterestResult(state)
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
                CompoundInterestInputs(state, onAction)
                Spacer(modifier = Modifier.height(16.dp))
                CompoundInterestResult(state)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompoundInterestInputs(
    state: CompoundInterestState,
    onAction: (CompoundInterestEvent) -> Unit
) {
    val principalFocus = remember { FocusRequester() }
    val rateFocus = remember { FocusRequester() }
    val timeFocus = remember { FocusRequester() }

    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CyberpunkInput(
                value = if (state.principal == 0.0) "" else if (state.principal % 1.0 == 0.0) state.principal.toInt().toString() else state.principal.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(CompoundInterestEvent.UpdatePrincipal(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(CompoundInterestEvent.UpdatePrincipal(num))
                        }
                    }
                },
                label = stringResource(R.string.principal_amount_label).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(principalFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            CyberpunkInput(
                value = if (state.rate == 0.0) "" else if (state.rate % 1.0 == 0.0) state.rate.toInt().toString() else state.rate.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(CompoundInterestEvent.UpdateRate(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(CompoundInterestEvent.UpdateRate(num))
                        }
                    }
                },
                label = stringResource(R.string.rate_of_interest_label).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(rateFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next
                )
            )

            CyberpunkInput(
                value = if (state.timeYears == 0.0) "" else if (state.timeYears % 1.0 == 0.0) state.timeYears.toInt().toString() else state.timeYears.toString(),
                onValueChange = {
                    if (it.isEmpty()) {
                        onAction(CompoundInterestEvent.UpdateTime(0.0))
                    } else {
                        it.toDoubleOrNull()?.let { num ->
                            onAction(CompoundInterestEvent.UpdateTime(num))
                        }
                    }
                },
                label = stringResource(R.string.time_period_years_label).uppercase(),
                modifier = Modifier.fillMaxWidth().focusRequester(timeFocus),
                borderColor = MaterialTheme.colorScheme.primary,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                )
            )

            // Frequency Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TechText(
                    text = stringResource(R.string.compounding_frequency_label).uppercase(),
                    fontSize = 14.sp,
                    color = CyberpunkTextSecondary
                )

                var expanded = remember { androidx.compose.runtime.mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded.value,
                    onExpandedChange = { expanded.value = !expanded.value },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = getFrequencyLabel(state.frequency).uppercase(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                    ) {
                        CompoundingFrequency.values().forEach { frequency ->
                            DropdownMenuItem(
                                text = { TechText(getFrequencyLabel(frequency).uppercase(), color = MaterialTheme.colorScheme.onSurface) },
                                onClick = {
                                    onAction(CompoundInterestEvent.UpdateFrequency(frequency))
                                    expanded.value = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun getFrequencyLabel(frequency: CompoundingFrequency): String {
    return when (frequency) {
        CompoundingFrequency.YEARLY -> stringResource(R.string.frequency_yearly)
        CompoundingFrequency.HALF_YEARLY -> stringResource(R.string.frequency_half_yearly)
        CompoundingFrequency.QUARTERLY -> stringResource(R.string.frequency_quarterly)
        CompoundingFrequency.MONTHLY -> stringResource(R.string.frequency_monthly)
    }
}

@Composable
private fun CompoundInterestResult(state: CompoundInterestState) {
    CyberpunkCard(
        borderColor = MaterialTheme.colorScheme.secondary,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ResultRow(stringResource(R.string.principal_amount_label).uppercase(), state.principal)
            ResultRow(stringResource(R.string.interest_earned_label).uppercase(), state.interest)

            Spacer(modifier = Modifier.height(16.dp))

            PieChart(
                data = listOf(
                    PieChartData(state.principal, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), stringResource(R.string.principal).uppercase()),
                    PieChartData(state.interest, MaterialTheme.colorScheme.secondary, stringResource(R.string.interest).uppercase())
                ),
                chartSize = 200.dp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TechText(
                    text = stringResource(R.string.total_amount_label).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                TechText(
                    text = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(state.totalAmount),
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: Double) {
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
            text = NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(value),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
