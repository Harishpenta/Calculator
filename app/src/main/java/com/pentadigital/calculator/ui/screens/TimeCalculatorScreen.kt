package com.pentadigital.calculator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pentadigital.calculator.R
import com.pentadigital.calculator.ui.theme.*
import com.pentadigital.calculator.ui.components.*
import com.pentadigital.calculator.utils.isLandscape
import com.pentadigital.calculator.viewmodels.TimeCalculatorEvent
import com.pentadigital.calculator.viewmodels.TimeCalculatorState
import com.pentadigital.calculator.viewmodels.TimeOperation

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeCalculatorScreen(
    state: TimeCalculatorState,
    onAction: (TimeCalculatorEvent) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isLandscape = isLandscape()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TechText(
                        stringResource(R.string.time_calculator_title).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        fontSize = 20.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = NeonCyan
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
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimeInputs(state, onAction)
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    TimeResults(state)
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
                TimeInputs(state, onAction)
                TimeResults(state)
            }
        }
    }
}

@Composable
private fun TimeInputs(
    state: TimeCalculatorState,
    onAction: (TimeCalculatorEvent) -> Unit
) {
    val h1Focus = remember { FocusRequester() }
    val m1Focus = remember { FocusRequester() }
    val s1Focus = remember { FocusRequester() }
    
    val h2Focus = remember { FocusRequester() }
    val m2Focus = remember { FocusRequester() }
    val s2Focus = remember { FocusRequester() }

    CyberpunkCard(
        borderColor = NeonPurple,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Time 1
            TimeInputSection(
                title = stringResource(R.string.time_1).uppercase(),
                hour = state.hour1,
                minute = state.minute1,
                second = state.second1,
                onHourChange = { onAction(TimeCalculatorEvent.UpdateTime1(it, state.minute1, state.second1)) },
                onMinuteChange = { onAction(TimeCalculatorEvent.UpdateTime1(state.hour1, it, state.second1)) },
                onSecondChange = { onAction(TimeCalculatorEvent.UpdateTime1(state.hour1, state.minute1, it)) },
                hFocus = h1Focus,
                mFocus = m1Focus,
                sFocus = s1Focus,
                nextFocus = h2Focus
            )

            // Operation Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                OperationToggle(
                    selectedOperation = state.operation,
                    onOperationSelected = { onAction(TimeCalculatorEvent.UpdateOperation(it)) }
                )
            }

            // Time 2
            TimeInputSection(
                title = stringResource(R.string.time_2).uppercase(),
                hour = state.hour2,
                minute = state.minute2,
                second = state.second2,
                onHourChange = { onAction(TimeCalculatorEvent.UpdateTime2(it, state.minute2, state.second2)) },
                onMinuteChange = { onAction(TimeCalculatorEvent.UpdateTime2(state.hour2, it, state.second2)) },
                onSecondChange = { onAction(TimeCalculatorEvent.UpdateTime2(state.hour2, state.minute2, it)) },
                hFocus = h2Focus,
                mFocus = m2Focus,
                sFocus = s2Focus,
                nextFocus = null
            )
        }
    }
}

@Composable
private fun TimeInputSection(
    title: String,
    hour: String,
    minute: String,
    second: String,
    onHourChange: (String) -> Unit,
    onMinuteChange: (String) -> Unit,
    onSecondChange: (String) -> Unit,
    hFocus: FocusRequester,
    mFocus: FocusRequester,
    sFocus: FocusRequester,
    nextFocus: FocusRequester?
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        TechText(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = CyberpunkTextSecondary
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TimeTextField(
                value = hour,
                onValueChange = onHourChange,
                label = "HR",
                modifier = Modifier.weight(1f),
                focusRequester = hFocus
            )
            TimeTextField(
                value = minute,
                onValueChange = onMinuteChange,
                label = "MIN",
                modifier = Modifier.weight(1f),
                focusRequester = mFocus
            )
            TimeTextField(
                value = second,
                onValueChange = onSecondChange,
                label = "SEC",
                modifier = Modifier.weight(1f),
                focusRequester = sFocus,
                imeAction = if (nextFocus == null) ImeAction.Done else ImeAction.Next
            )
        }
    }
}

@Composable
private fun TimeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    focusRequester: FocusRequester,
    imeAction: ImeAction = ImeAction.Next
) {
    CyberpunkInput(
        value = value,
        onValueChange = { if (it.length <= 2 && it.all { char -> char.isDigit() }) onValueChange(it) },
        label = label,
        modifier = modifier.focusRequester(focusRequester),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = imeAction
        ),
        borderColor = NeonCyan
    )
}

@Composable
private fun OperationToggle(
    selectedOperation: TimeOperation,
    onOperationSelected: (TimeOperation) -> Unit
) {
    Row(
        modifier = Modifier
            .background(
                color = CyberpunkSurface.copy(alpha = 0.3f),
                shape = RoundedCornerShape(50)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        OperationButton(
            icon = rememberVectorPainter(Icons.Default.Add),
            text = stringResource(R.string.add).uppercase(),
            isSelected = selectedOperation == TimeOperation.ADD,
            onClick = { onOperationSelected(TimeOperation.ADD) }
        )
        OperationButton(
            icon = painterResource(R.drawable.ic_remove),
            text = stringResource(R.string.subtract).uppercase(),
            isSelected = selectedOperation == TimeOperation.SUBTRACT,
            onClick = { onOperationSelected(TimeOperation.SUBTRACT) }
        )
    }
}

@Composable
private fun OperationButton(
    icon: Painter,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) NeonCyan else Color.Transparent,
            contentColor = if (isSelected) CyberpunkDarkBG else NeonCyan
        ),
        shape = RoundedCornerShape(50),
        elevation = null,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        TechText(text = text, color = if (isSelected) CyberpunkDarkBG else NeonCyan, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TimeResults(state: TimeCalculatorState) {
    CyberpunkCard(
        borderColor = NeonGreen,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TechText(
                text = stringResource(R.string.total_time).uppercase(),
                color = CyberpunkTextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            val sign = if (state.totalSeconds < 0) "-" else ""
            val h = state.resultHour
            val m = state.resultMinute
            val s = state.resultSecond
            
            TechText(
                text = "$sign${h}h ${m}m ${s}s",
                color = NeonGreen,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
