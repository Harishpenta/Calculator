package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class WaterIntakeState(
    val weight: String = "70.0",
    val activityLevel: ActivityLevel = ActivityLevel.SEDENTARY,
    val climate: Climate = Climate.NORMAL,
    val waterIntakeLiters: Double = 0.0,
    val waterIntakeCups: Int = 0 // Assuming 250ml cup
)

enum class Climate {
    NORMAL, HOT
}

sealed class WaterIntakeEvent {
    data class UpdateWeight(val weight: String) : WaterIntakeEvent()
    data class UpdateActivityLevel(val level: ActivityLevel) : WaterIntakeEvent()
    data class UpdateClimate(val climate: Climate) : WaterIntakeEvent()
    object Calculate : WaterIntakeEvent()
    object Clear : WaterIntakeEvent()
}

class WaterIntakeViewModel : ViewModel() {
    var state by mutableStateOf(WaterIntakeState())
        private set

    fun onEvent(event: WaterIntakeEvent) {
        when (event) {
            is WaterIntakeEvent.UpdateWeight -> state = state.copy(weight = event.weight)
            is WaterIntakeEvent.UpdateActivityLevel -> state = state.copy(activityLevel = event.level)
            is WaterIntakeEvent.UpdateClimate -> state = state.copy(climate = event.climate)
            is WaterIntakeEvent.Calculate -> calculateWaterIntake()
            is WaterIntakeEvent.Clear -> state = WaterIntakeState()
        }
    }

    private fun calculateWaterIntake() {
        val weight = state.weight.toDoubleOrNull() ?: return
        
        // Base calculation: 33ml per kg
        var intake = weight * 0.033

        // Activity adjustment (simplified)
        // Sedentary: base
        // Lightly Active: +0.35L
        // Moderately Active: +0.7L
        // Very Active: +1.0L
        // Extra Active: +1.4L
        intake += when (state.activityLevel) {
            ActivityLevel.SEDENTARY -> 0.0
            ActivityLevel.LIGHTLY_ACTIVE -> 0.35
            ActivityLevel.MODERATELY_ACTIVE -> 0.7
            ActivityLevel.VERY_ACTIVE -> 1.0
            ActivityLevel.EXTRA_ACTIVE -> 1.4
        }

        // Climate adjustment
        if (state.climate == Climate.HOT) {
            intake += 0.5
        }

        val cups = (intake * 1000 / 250).toInt()

        state = state.copy(
            waterIntakeLiters = intake,
            waterIntakeCups = cups
        )
    }
}
