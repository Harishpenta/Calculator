package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.pow

data class BmiState(
    val weightKg: Double = 70.0,
    val heightCm: Double = 170.0,
    val bmi: Double = 0.0,
    val category: String = ""
)

class BmiViewModel : ViewModel() {
    var state by mutableStateOf(BmiState())
        private set

    init {
        calculateBmi()
    }

    fun onEvent(event: BmiEvent) {
        when (event) {
            is BmiEvent.UpdateWeight -> {
                state = state.copy(weightKg = event.weight)
                calculateBmi()
            }
            is BmiEvent.UpdateHeight -> {
                state = state.copy(heightCm = event.height)
                calculateBmi()
            }
        }
    }

    private fun calculateBmi() {
        val weight = state.weightKg
        val heightM = state.heightCm / 100.0
        
        if (heightM > 0) {
            val bmi = weight / heightM.pow(2)
            val category = when {
                bmi < 18.5 -> "Underweight"
                bmi < 25.0 -> "Normal"
                bmi < 30.0 -> "Overweight"
                else -> "Obese"
            }
            state = state.copy(bmi = bmi, category = category)
        }
    }
}

sealed class BmiEvent {
    data class UpdateWeight(val weight: Double) : BmiEvent()
    data class UpdateHeight(val height: Double) : BmiEvent()
}
