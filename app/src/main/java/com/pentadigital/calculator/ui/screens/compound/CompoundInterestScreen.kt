package com.pentadigital.calculator.ui.screens.compound

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pentadigital.calculator.domain.model.CompoundingFrequency
import com.pentadigital.calculator.domain.model.ContributionFrequency
import com.pentadigital.calculator.domain.model.ContributionType
import com.pentadigital.calculator.ui.components.*

@Composable
fun CompoundInterestScreen(
    viewModel: CompoundInterestViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TechDisplayContainer(modifier = Modifier.fillMaxWidth()) {
            TechText(
                text = "COMPOUND INTEREST",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            GlowingDivider()
            Spacer(modifier = Modifier.height(16.dp))
            TechText(text = "ACCURATE FINANCIAL PROJECTION", fontSize = 12.sp, color = Color.Gray)
        }

        // Input Section
        CicInputSection(state, viewModel)

        // Contribution Section
        CicContributionSection(state, viewModel)

        // Only show results if we have calculated something valid
        if (state.result != null) {
            GlowingDivider(modifier = Modifier.padding(vertical = 16.dp))
            CicResultSection(state)

            Spacer(modifier = Modifier.height(16.dp))
            CicBreakdownSection(state, viewModel)
        }
        
        Spacer(modifier = Modifier.height(100.dp)) // padding for bottom nav
    }
}

@Composable
fun CicInputSection(
    state: CompoundInterestState,
    viewModel: CompoundInterestViewModel
) {
    CyberpunkCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TechText("PRIMARY INVESTMENT", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            HelpTooltip("Initial amount you plan to invest plus the expected interest rate.")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Currency Dropdown
            CurrencyDropdown(
                selected = state.selectedCurrency,
                options = CurrencyInfo.values().toList(),
                onSelect = { viewModel.onEvent(CompoundInterestEvent.UpdateCurrency(it)) },
                modifier = Modifier.weight(0.45f)
            )
            
            CyberpunkInput(
                value = state.principalInput,
                onValueChange = { viewModel.onEvent(CompoundInterestEvent.UpdatePrincipal(it)) },
                label = "Initial Amount",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(0.65f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CyberpunkInput(
                value = state.interestRateInput,
                onValueChange = { viewModel.onEvent(CompoundInterestEvent.UpdateInterestRate(it)) },
                label = "Interest Rate (P.A. %)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(0.5f)
            )

            Column(modifier = Modifier.weight(0.5f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TechText("Compounding", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    HelpTooltip("How often interest is calculated and added to the principal.")
                }
                Spacer(modifier = Modifier.height(4.dp))
                FrequencyDropdown(
                    selected = state.compoundingFrequency.name,
                    options = CompoundingFrequency.values().map { it.name },
                    onSelect = { viewModel.onEvent(CompoundInterestEvent.UpdateCompoundingFrequency(CompoundingFrequency.valueOf(it))) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CyberpunkInput(
                value = state.durationYearsInput,
                onValueChange = { viewModel.onEvent(CompoundInterestEvent.UpdateDurationYears(it)) },
                label = "Duration (Years)",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.5f)
            )
            CyberpunkInput(
                value = state.durationMonthsInput,
                onValueChange = { viewModel.onEvent(CompoundInterestEvent.UpdateDurationMonths(it)) },
                label = "Months",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(0.5f)
            )
        }
    }
}

@Composable
fun HelpTooltip(text: String) {
    var showDialog by remember { mutableStateOf(false) }
    
    IconButton(
        onClick = { showDialog = true },
        modifier = Modifier.size(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = "Help",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(14.dp)
        )
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK", color = MaterialTheme.colorScheme.primary)
                }
            },
            title = { TechText("Financial Tip", color = MaterialTheme.colorScheme.primary) },
            text = { Text(text, color = Color.LightGray) },
            containerColor = Color(0xFF0A0A12),
            titleContentColor = MaterialTheme.colorScheme.primary,
            textContentColor = Color.LightGray
        )
    }
}

@Composable
fun CicContributionSection(
    state: CompoundInterestState,
    viewModel: CompoundInterestViewModel
) {
    CyberpunkCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TechText("REGULAR CONTRIBUTIONS", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            HelpTooltip("Extra money you add (+) or take out (-) periodically.")
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CyberpunkButton(
                text = "NONE",
                onClick = { viewModel.onEvent(CompoundInterestEvent.UpdateContributionType(ContributionType.NONE)) },
                color = if (state.contributionType == ContributionType.NONE) MaterialTheme.colorScheme.primary else Color.Gray
            )
            CyberpunkButton(
                text = "(+)",
                onClick = { viewModel.onEvent(CompoundInterestEvent.UpdateContributionType(ContributionType.DEPOSIT)) },
                color = if (state.contributionType == ContributionType.DEPOSIT) Color(0xFF32CD32) else Color.Gray
            )
            CyberpunkButton(
                text = "(-)",
                onClick = { viewModel.onEvent(CompoundInterestEvent.UpdateContributionType(ContributionType.WITHDRAWAL)) },
                color = if (state.contributionType == ContributionType.WITHDRAWAL) Color.Red else Color.Gray
            )
        }

        if (state.contributionType != ContributionType.NONE) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CyberpunkInput(
                    value = state.contributionAmountInput,
                    onValueChange = { viewModel.onEvent(CompoundInterestEvent.UpdateContributionAmount(it)) },
                    label = "Amount (${state.selectedCurrency.symbol})",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(0.5f)
                )

                Column(modifier = Modifier.weight(0.5f)) {
                    TechText("Frequency", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    FrequencyDropdown(
                        selected = state.contributionFrequency.name,
                        options = ContributionFrequency.values().map { it.name },
                        onSelect = { viewModel.onEvent(CompoundInterestEvent.UpdateContributionFrequency(ContributionFrequency.valueOf(it))) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                CyberpunkInput(
                    value = state.annualContributionIncreaseRateInput,
                    onValueChange = { viewModel.onEvent(CompoundInterestEvent.UpdateAnnualIncreaseRate(it)) },
                    label = "Annual Plan Increase (%)",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                HelpTooltip("The percentage by which your contribution increases each year (e.g., to keep up with inflation).")
            }
        }
    }
}

// Very basic dropdown using standard Compose Material 3 
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FrequencyDropdown(
    selected: String,
    options: List<String>,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selected.lowercase().capitalize(),
            onValueChange = {},
            readOnly = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = androidx.compose.foundation.shape.CutCornerShape(8.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { TechText(option.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencyDropdown(
    selected: CurrencyInfo,
    options: List<CurrencyInfo>,
    onSelect: (CurrencyInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = "${selected.symbol} ${selected.code}",
            onValueChange = {},
            readOnly = true,
            label = { TechText("Crncy", color = Color.Gray, fontSize = 12.sp) },
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, fontSize = 16.sp, fontWeight = FontWeight.Bold),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            ),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = androidx.compose.foundation.shape.CutCornerShape(8.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { TechText("${option.symbol} - ${option.code}") },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    }
                )
            }
        }
    }
}
