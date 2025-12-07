package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class TimeCalculatorState(
    val hour1: String = "",
    val minute1: String = "",
    val second1: String = "",
    val hour2: String = "",
    val minute2: String = "",
    val second2: String = "",
    val operation: TimeOperation = TimeOperation.ADD,
    val resultHour: Int = 0,
    val resultMinute: Int = 0,
    val resultSecond: Int = 0,
    val totalSeconds: Long = 0
)

enum class TimeOperation {
    ADD, SUBTRACT
}

sealed class TimeCalculatorEvent {
    data class UpdateTime1(val hour: String, val minute: String, val second: String) : TimeCalculatorEvent()
    data class UpdateTime2(val hour: String, val minute: String, val second: String) : TimeCalculatorEvent()
    data class UpdateOperation(val operation: TimeOperation) : TimeCalculatorEvent()
    object Clear : TimeCalculatorEvent()
}

class TimeCalculatorViewModel : ViewModel() {
    var state by mutableStateOf(TimeCalculatorState())
        private set

    fun onEvent(event: TimeCalculatorEvent) {
        when (event) {
            is TimeCalculatorEvent.UpdateTime1 -> {
                state = state.copy(hour1 = event.hour, minute1 = event.minute, second1 = event.second)
                calculateTime()
            }
            is TimeCalculatorEvent.UpdateTime2 -> {
                state = state.copy(hour2 = event.hour, minute2 = event.minute, second2 = event.second)
                calculateTime()
            }
            is TimeCalculatorEvent.UpdateOperation -> {
                state = state.copy(operation = event.operation)
                calculateTime()
            }
            is TimeCalculatorEvent.Clear -> {
                state = TimeCalculatorState()
            }
        }
    }

    private fun calculateTime() {
        val h1 = state.hour1.toIntOrNull() ?: 0
        val m1 = state.minute1.toIntOrNull() ?: 0
        val s1 = state.second1.toIntOrNull() ?: 0
        
        val h2 = state.hour2.toIntOrNull() ?: 0
        val m2 = state.minute2.toIntOrNull() ?: 0
        val s2 = state.second2.toIntOrNull() ?: 0

        val totalSeconds1 = (h1 * 3600L) + (m1 * 60L) + s1
        val totalSeconds2 = (h2 * 3600L) + (m2 * 60L) + s2

        val resultTotalSeconds = if (state.operation == TimeOperation.ADD) {
            totalSeconds1 + totalSeconds2
        } else {
            if (totalSeconds1 >= totalSeconds2) {
                totalSeconds1 - totalSeconds2
            } else {
                // Handle negative time? For now, let's just show absolute difference or 0
                // Typically time calculators show negative or absolute.
                // Let's go with absolute difference for simplicity or 0 if negative is not desired.
                // But usually subtraction implies duration.
                // Let's allow negative result conceptually but display as positive with a sign?
                // Or just absolute difference. Let's do absolute difference to avoid confusion.
                // Or better, standard subtraction:
                totalSeconds1 - totalSeconds2
            }
        }
        
        val absSeconds = kotlin.math.abs(resultTotalSeconds)
        val hours = (absSeconds / 3600).toInt()
        val minutes = ((absSeconds % 3600) / 60).toInt()
        val seconds = (absSeconds % 60).toInt()

        state = state.copy(
            resultHour = hours,
            resultMinute = minutes,
            resultSecond = seconds,
            totalSeconds = resultTotalSeconds
        )
    }
}
