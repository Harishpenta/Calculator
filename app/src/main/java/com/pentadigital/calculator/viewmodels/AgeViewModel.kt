package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.Period

data class AgeState(
    val birthDate: LocalDate = LocalDate.now(),
    val ageYears: Int = 0,
    val ageMonths: Int = 0,
    val ageDays: Int = 0
)

class AgeViewModel : ViewModel() {
    var state by mutableStateOf(AgeState())
        private set

    fun onEvent(event: AgeEvent) {
        when (event) {
            is AgeEvent.UpdateBirthDate -> {
                calculateAge(event.date)
            }
        }
    }

    private fun calculateAge(birthDate: LocalDate) {
        val currentDate = LocalDate.now()
        val period = Period.between(birthDate, currentDate)
        
        state = state.copy(
            birthDate = birthDate,
            ageYears = period.years,
            ageMonths = period.months,
            ageDays = period.days
        )
    }
}

sealed class AgeEvent {
    data class UpdateBirthDate(val date: LocalDate) : AgeEvent()
}
