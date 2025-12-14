package com.pentadigital.calculator.ui.screens

import com.pentadigital.calculator.viewmodels.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.utils.*
import com.pentadigital.calculator.ui.theme.*
import android.app.DatePickerDialog
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import androidx.compose.foundation.layout.WindowInsets

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeScreen(
    state: AgeState,
    onAction: (AgeEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            onAction(AgeEvent.UpdateBirthDate(LocalDate.of(year, month + 1, dayOfMonth)))
        },
        state.birthDate.year,
        state.birthDate.monthValue - 1,
        state.birthDate.dayOfMonth
    )
    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    TechText(
                        stringResource(com.pentadigital.calculator.R.string.age_title).uppercase(), 
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
                    val shareBodyTemplate = stringResource(com.pentadigital.calculator.R.string.age_share_body)
                    val shareTitle = stringResource(com.pentadigital.calculator.R.string.age_result_title)
                    val shareIconDesc = stringResource(com.pentadigital.calculator.R.string.share)

                    IconButton(onClick = {
                        val shareBody = String.format(
                            shareBodyTemplate,
                            "${state.birthDate}",
                            "${state.ageYears}",
                            "${state.ageMonths}",
                            "${state.ageDays}"
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
                    // Date Picker
                    TechText(
                        text = stringResource(com.pentadigital.calculator.R.string.date_of_birth).uppercase(),
                        color = CyberpunkTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    CyberpunkCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        borderColor = NeonPurple
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TechText(
                                text = state.birthDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = stringResource(com.pentadigital.calculator.R.string.select_date),
                                tint = NeonPurple
                            )
                        }
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
                        borderColor = NeonGreen
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(24.dp)
                        ) {
                            TechText(
                                text = stringResource(com.pentadigital.calculator.R.string.your_age).uppercase(),
                                color = CyberpunkTextSecondary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                AgeStatItem(value = state.ageYears, label = stringResource(com.pentadigital.calculator.R.string.years))
                                AgeStatItem(value = state.ageMonths, label = stringResource(com.pentadigital.calculator.R.string.months))
                                AgeStatItem(value = state.ageDays, label = stringResource(com.pentadigital.calculator.R.string.days))
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Placeholder for Gauge - could be replaced with a Cyberpunk specific one later
                            // For now, we'll keep the logic but style it if possible, or leave as is if it's a custom drawing
                            AgeProgressGauge(
                                daysToNextBirthday = calculateDaysToNextBirthday(state.birthDate),
                                size = 180.dp
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
                // Date Picker
                TechText(
                    text = stringResource(com.pentadigital.calculator.R.string.date_of_birth).uppercase(),
                    color = CyberpunkTextSecondary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                CyberpunkCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() },
                    borderColor = NeonPurple
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TechText(
                            text = state.birthDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
                            color = NeonCyan,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = stringResource(com.pentadigital.calculator.R.string.select_date),
                            tint = NeonPurple
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Result Section
                CyberpunkCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = NeonGreen
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        TechText(
                            text = stringResource(com.pentadigital.calculator.R.string.your_age).uppercase(),
                            color = CyberpunkTextSecondary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            AgeStatItem(value = state.ageYears, label = stringResource(com.pentadigital.calculator.R.string.years))
                            AgeStatItem(value = state.ageMonths, label = stringResource(com.pentadigital.calculator.R.string.months))
                            AgeStatItem(value = state.ageDays, label = stringResource(com.pentadigital.calculator.R.string.days))
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        AgeProgressGauge(
                            daysToNextBirthday = calculateDaysToNextBirthday(state.birthDate),
                            size = 200.dp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AgeStatItem(
    value: Int,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TechText(
            text = value.toString(),
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = NeonGreen
        )
        TechText(
            text = label.uppercase(),
            fontSize = 14.sp,
            color = CyberpunkTextSecondary,
            fontWeight = FontWeight.Medium
        )
    }
}

fun calculateDaysToNextBirthday(birthDate: LocalDate): Int {
    val today = LocalDate.now()
    val currentYearBirthday = birthDate.withYear(today.year)
    
    return if (currentYearBirthday.isEqual(today) || currentYearBirthday.isAfter(today)) {
        java.time.temporal.ChronoUnit.DAYS.between(today, currentYearBirthday).toInt()
    } else {
        val nextYearBirthday = birthDate.withYear(today.year + 1)
        java.time.temporal.ChronoUnit.DAYS.between(today, nextYearBirthday).toInt()
    }
}
