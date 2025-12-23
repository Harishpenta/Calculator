package com.pentadigital.calculator.ui.screens

import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.ui.theme.*
import com.pentadigital.calculator.utils.isLandscape
import com.pentadigital.calculator.viewmodels.DateDifferenceEvent
import com.pentadigital.calculator.viewmodels.DateDifferenceState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateDifferenceScreen(
    state: DateDifferenceState,
    onAction: (DateDifferenceEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.date_difference_title).uppercase(),
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
                    DateInputs(state, onAction)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DateResults(state)
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
                DateInputs(state, onAction)
                DateResults(state)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun DateInputs(
    state: DateDifferenceState,
    onAction: (DateDifferenceEvent) -> Unit
) {
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy")

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onAction(DateDifferenceEvent.UpdateStartDate(LocalDate.of(year, month + 1, dayOfMonth)))
        },
        state.startDate.year,
        state.startDate.monthValue - 1,
        state.startDate.dayOfMonth
    )

    val endDatePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onAction(DateDifferenceEvent.UpdateEndDate(LocalDate.of(year, month + 1, dayOfMonth)))
        },
        state.endDate.year,
        state.endDate.monthValue - 1,
        state.endDate.dayOfMonth
    )

    CyberpunkCard(
        borderColor = NeonPurple,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Start Date
            DateInputRow(
                label = stringResource(R.string.start_date).uppercase(),
                date = state.startDate.format(dateFormatter),
                onClick = { startDatePickerDialog.show() }
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))

            // End Date
            DateInputRow(
                label = stringResource(R.string.end_date).uppercase(),
                date = state.endDate.format(dateFormatter),
                onClick = { endDatePickerDialog.show() }
            )
        }
    }
}

@Composable
private fun DateInputRow(
    label: String,
    date: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TechText(
            text = label,
            color = CyberpunkTextSecondary,
            fontSize = 14.sp
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TechText(
                text = date,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun DateResults(state: DateDifferenceState) {
    CyberpunkCard(
        borderColor = NeonGreen,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TechText(
                text = stringResource(R.string.difference).uppercase(),
                color = CyberpunkTextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            val years = abs(state.years)
            val months = abs(state.months)
            val days = abs(state.days)

            val parts = mutableListOf<String>()
            if (years > 0) parts.add("$years ${if (years == 1) "Year" else "Years"}")
            if (months > 0) parts.add("$months ${if (months == 1) "Month" else "Months"}")
            if (days > 0) parts.add("$days ${if (days == 1) "Day" else "Days"}")

            if (parts.isEmpty()) parts.add("0 Days")

            TechText(
                text = parts.joinToString(", "),
                color = NeonGreen,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
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
                label = stringResource(R.string.total_days).uppercase(),
                value = "${abs(state.totalDays)} Days",
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
