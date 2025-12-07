package com.pentadigital.calculator.viewmodels

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.Period
import java.time.temporal.ChronoUnit

data class DateDifferenceState(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val years: Int = 0,
    val months: Int = 0,
    val days: Int = 0,
    val totalDays: Long = 0
)

sealed class DateDifferenceEvent {
    data class UpdateStartDate(val date: LocalDate) : DateDifferenceEvent()
    data class UpdateEndDate(val date: LocalDate) : DateDifferenceEvent()
}

@RequiresApi(Build.VERSION_CODES.O)
class DateDifferenceViewModel : ViewModel() {
    var state by mutableStateOf(DateDifferenceState())
        private set

    fun onEvent(event: DateDifferenceEvent) {
        when (event) {
            is DateDifferenceEvent.UpdateStartDate -> {
                state = state.copy(startDate = event.date)
                calculateDifference()
            }
            is DateDifferenceEvent.UpdateEndDate -> {
                state = state.copy(endDate = event.date)
                calculateDifference()
            }
        }
    }

    private fun calculateDifference() {
        val start = state.startDate
        val end = state.endDate

        // Ensure start is before end for calculation, or handle negative?
        // Usually date difference is absolute or directional.
        // Period.between(start, end) handles direction.
        
        val period = Period.between(start, end)
        val totalDays = ChronoUnit.DAYS.between(start, end)

        state = state.copy(
            years = period.years,
            months = period.months,
            days = period.days,
            totalDays = totalDays
        )
    }
}
