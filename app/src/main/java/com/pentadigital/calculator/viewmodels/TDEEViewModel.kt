package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class TDEEState(
    val gender: Gender = Gender.MALE,
    val userAge: String = "25",
    val weight: String = "70.0",
    val height: String = "170.0",
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val tdee: Int = 0,
    val bmr: Int = 0,
    val maintenance: Int = 0,
    val cutting: Int = 0,
    val bulking: Int = 0
)

enum class Gender {
    MALE, FEMALE
}

enum class ActivityLevel(val multiplier: Double, val label: String) {
    SEDENTARY(1.2, "Sedentary (little or no exercise)"),
    LIGHTLY_ACTIVE(1.375, "Lightly active (1-3 days/week)"),
    MODERATELY_ACTIVE(1.55, "Moderately active (3-5 days/week)"),
    VERY_ACTIVE(1.725, "Very active (6-7 days/week)"),
    EXTRA_ACTIVE(1.9, "Extra active (very hard exercise/job)")
}

sealed class TDEEEvent {
    data class UpdateGender(val gender: Gender) : TDEEEvent()
    data class UpdateAgeValue(val newAge: String) : TDEEEvent()
    data class UpdateWeight(val weight: String) : TDEEEvent()
    data class UpdateHeight(val height: String) : TDEEEvent()
    data class UpdateActivityLevel(val level: ActivityLevel) : TDEEEvent()
    object Calculate : TDEEEvent()
    object Clear : TDEEEvent()
}

class TDEEViewModel : ViewModel() {
    var state by mutableStateOf(TDEEState())
        private set

    fun onEvent(event: TDEEEvent) {
        when (event) {
            is TDEEEvent.UpdateGender -> state = state.copy(gender = event.gender)
            is TDEEEvent.UpdateAgeValue -> state = state.copy(userAge = event.newAge)
            is TDEEEvent.UpdateWeight -> state = state.copy(weight = event.weight)
            is TDEEEvent.UpdateHeight -> state = state.copy(height = event.height)
            is TDEEEvent.UpdateActivityLevel -> state = state.copy(activityLevel = event.level)
            is TDEEEvent.Calculate -> calculateTDEE()
            is TDEEEvent.Clear -> state = TDEEState()
        }
    }

    private fun calculateTDEE() {
        val age = state.userAge.toIntOrNull() ?: return
        val weight = state.weight.toDoubleOrNull() ?: return
        val height = state.height.toDoubleOrNull() ?: return

        // Mifflin-St Jeor Equation
        val s = if (state.gender == Gender.MALE) 5 else -161
        val bmr = (10 * weight) + (6.25 * height) - (5 * age) + s
        
        val tdee = bmr * state.activityLevel.multiplier

        state = state.copy(
            bmr = bmr.toInt(),
            tdee = tdee.toInt(),
            maintenance = tdee.toInt(),
            cutting = (tdee - 500).toInt(),
            bulking = (tdee + 500).toInt()
        )
    }
}
