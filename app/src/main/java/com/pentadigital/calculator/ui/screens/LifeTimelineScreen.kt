package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.domain.LifeEvent
import com.pentadigital.calculator.domain.TimelineState
import com.pentadigital.calculator.ui.components.CyberpunkCard
import com.pentadigital.calculator.ui.components.TechText
import com.pentadigital.calculator.ui.theme.NeonBlue
import com.pentadigital.calculator.ui.theme.NeonCyan
import com.pentadigital.calculator.ui.theme.NeonGreen
import com.pentadigital.calculator.ui.theme.NeonPurple
import com.pentadigital.calculator.ui.theme.NeonRed
import com.pentadigital.calculator.ui.theme.CyberpunkDarkBG
import com.pentadigital.calculator.viewmodels.LifeTimelineViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LifeTimelineScreen(
    viewModel: LifeTimelineViewModel,
    onOpenDrawer: () -> Unit
) {
    var showSettingsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.life_timeline_title),
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
                    IconButton(onClick = { showSettingsDialog = true }) {
                        Icon(
                            Icons.Filled.Settings,
                            contentDescription = stringResource(R.string.personalize_timeline),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                windowInsets = WindowInsets(0.dp)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Timeline Visualization
            CyberpunkCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp), // Height for the timeline
                borderColor = NeonPurple
            ) {
                TimelineCanvas(state = viewModel.state)
            }

            // Simulation Controls
            SimulationControls(
                monthlySavings = viewModel.state.monthlySavings,
                onSavingsChange = { viewModel.onSimulationChange(it) }
            )

            // Insights
            TimelineInsights(state = viewModel.state)
        }
    }

    if (showSettingsDialog) {
        TimelineSettingsDialog(
            initialDob = viewModel.state.events.firstOrNull { it.title == "Now" }?.age?.let { System.currentTimeMillis() - (it * 31556952000L).toLong() } ?: System.currentTimeMillis(), // Approximate if not exact
            initialRetirementAge = viewModel.state.primeHealthEndAge, // Default to prime health end if not separate
            initialGoal = viewModel.state.events.find { it.title == "Goal Reached" }?.amount ?: 10000000.0,
            initialSavings = viewModel.state.monthlySavings,
            onDismiss = { showSettingsDialog = false },
            onSave = { dob, retAge, goal, savings ->
                viewModel.saveSettings(dob, retAge, goal, savings)
                showSettingsDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineSettingsDialog(
    initialDob: Long,
    initialRetirementAge: Int,
    initialGoal: Double,
    initialSavings: Double,
    onDismiss: () -> Unit,
    onSave: (Long, Int, Double, Double) -> Unit
) {
    var dob by remember { mutableLongStateOf(initialDob) }
    var retirementAge by remember { mutableIntStateOf(initialRetirementAge) }
    var goalAmount by remember { mutableDoubleStateOf(initialGoal) }
    var currentSavings by remember { mutableDoubleStateOf(initialSavings) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dateState = rememberDatePickerState(initialSelectedDateMillis = initialDob)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let { dob = it }
                    showDatePicker = false
                }) {
                    Text(stringResource(R.string.ok_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.personalize_timeline)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // DOB
                OutlinedTextField(
                    value = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date(dob)),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.date_of_birth)) },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(Icons.Filled.DateRange, stringResource(R.string.select_dob))
                        }
                    },
                    modifier = Modifier.fillMaxWidth().pointerInput(Unit) {
                         detectHorizontalDragGestures { _, _ -> /* Consume gestures */ }
                         // Or Clickable
                    }
                )
                Button(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.select_dob))
                }

                // Retirement Age
                Text(stringResource(R.string.retirement_goal_age_label, retirementAge))
                Slider(
                    value = retirementAge.toFloat(),
                    onValueChange = { retirementAge = it.roundToInt() },
                    valueRange = 40f..80f,
                    steps = 39
                )

                // Financial Goal
                OutlinedTextField(
                    value = java.math.BigDecimal.valueOf(goalAmount).stripTrailingZeros().toPlainString(),
                    onValueChange = { goalAmount = it.toDoubleOrNull() ?: 0.0 },
                    label = { Text(stringResource(R.string.financial_goal_label)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Current Monthly Savings
                 OutlinedTextField(
                    value = java.math.BigDecimal.valueOf(currentSavings).stripTrailingZeros().toPlainString(),
                    onValueChange = { currentSavings = it.toDoubleOrNull() ?: 0.0 },
                    label = { Text(stringResource(R.string.current_savings_label)) },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(dob, retirementAge, goalAmount, currentSavings) }) {
                Text(stringResource(R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun TimelineCanvas(state: TimelineState) {
    val textPaint = remember {
        android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 30f
            textAlign = android.graphics.Paint.Align.CENTER
        }
    }
    
    // Resolve strings for canvas
    val eventsResolved = state.events.map { event ->
        val title = event.titleRes?.let { androidx.compose.ui.platform.LocalContext.current.getString(it) } ?: event.title
        event to title
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val w = size.width
        val h = size.height
        val centerY = h / 2
        
        // Define Time Range: Start (Current Age - 5) to End (Life Expectancy + 5)
        val startAge = (state.currentAge - 5).coerceAtLeast(0)
        val endAge = state.lifeExpectancy + 5
        val totalYears = endAge - startAge
        val pxPerYear = w / totalYears

        // Draw Health Zones Background
        // Prime Health: StartAge to 60 (Green to Yellow fade)
        // Aging: 60 to LifeExp (Yellow to Red fade)
        
        // ... (Simplified gradient for V1)
        val primeEndX = (state.primeHealthEndAge - startAge) * pxPerYear
        drawRect(
            brush = Brush.horizontalGradient(
                0.0f to NeonGreen.copy(alpha = 0.1f),
                (primeEndX/w) to NeonCyan.copy(alpha = 0.1f),
                1.0f to NeonRed.copy(alpha = 0.1f)
            ),
            size = Size(w, h)
        )

        // Draw Timeline Track
        drawLine(
            color = Color.Gray.copy(alpha = 0.5f),
            start = Offset(0f, centerY),
            end = Offset(w, centerY),
            strokeWidth = 2.dp.toPx()
        )

        // Draw Age Ticks
        for (age in startAge..endAge step 5) {
            val x = (age - startAge) * pxPerYear
            drawLine(
                color = Color.Gray.copy(alpha = 0.3f),
                start = Offset(x, centerY - 10),
                end = Offset(x, centerY + 10),
                strokeWidth = 1.dp.toPx()
            )
            drawContext.canvas.nativeCanvas.drawText(
                age.toString(),
                x,
                centerY + 40f,
                textPaint.apply { textSize = 24f; color = android.graphics.Color.GRAY }
            )
        }

        // Draw Events
        eventsResolved.forEachIndexed { index, (event, title) ->
            val eventX = (event.age - startAge) * pxPerYear
            
            // Stagger heights to prevent overlap using 3 levels
            // Using Dp for consistent physical size
            val staggerIndex = index % 3
            val baseHeight = 30.dp.toPx()
            val heightStep = 28.dp.toPx()
            
            val stickEndOffset = baseHeight + (staggerIndex * heightStep)
            val labelOffset = stickEndOffset + 8.dp.toPx()
            
            // Flag Stick
            drawLine(
                color = event.color,
                start = Offset(eventX, centerY),
                end = Offset(eventX, centerY - stickEndOffset),
                strokeWidth = 2.dp.toPx()
            )
            
            // Flag Label
            drawContext.canvas.nativeCanvas.drawText(
                title,
                eventX,
                centerY - labelOffset,
                textPaint.apply { 
                    color = event.color.toArgb() 
                    textSize = 16.sp.toPx() 
                    isFakeBoldText = true 
                }
            )
            
            // Dot on line
            drawCircle(
                color = event.color,
                radius = 4.dp.toPx(),
                center = Offset(eventX, centerY)
            )
        }
    }
}

@Composable
fun SimulationControls(
    monthlySavings: Double,
    onSavingsChange: (Double) -> Unit
) {
    var sliderValue by remember(monthlySavings) { mutableFloatStateOf(monthlySavings.toFloat()) }

    CyberpunkCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = NeonCyan
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.simulation_monthly_investment),
                style = MaterialTheme.typography.titleMedium,
                color = NeonCyan
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "₹${sliderValue.toInt()}",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Slider(
                value = sliderValue,
                onValueChange = { sliderValue = it },
                onValueChangeFinished = { onSavingsChange(sliderValue.toDouble()) },
                valueRange = 0f..100000f,
                steps = 19, // Steps of 5000 approx
                colors = SliderDefaults.colors(
                    thumbColor = NeonCyan,
                    activeTrackColor = NeonCyan,
                    inactiveTrackColor = NeonCyan.copy(alpha = 0.2f)
                )
            )
            Text(
                text = stringResource(R.string.simulation_hint),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun TimelineInsights(state: TimelineState) {
    // Overlapping logic
    val freedomAge = state.goalAchievedAge ?: 100
    val primeEnd = state.primeHealthEndAge
    
    val overlapYears = (primeEnd - freedomAge).coerceAtLeast(0)
    
    CyberpunkCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (overlapYears > 0) NeonGreen else NeonRed
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(R.string.life_insight),
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            if (overlapYears > 0) {
                Text(
                    text = stringResource(R.string.prime_freedom_years, overlapYears),
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonGreen
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.prime_freedom_desc, freedomAge, primeEnd),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            } else {
                Text(
                    text = stringResource(R.string.prime_warning_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = NeonRed
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.prime_warning_desc, freedomAge, primeEnd),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White
                )
            }
        }
    }
}
