package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.log10

data class BodyFatState(
    val gender: Gender = Gender.MALE,
    val age: String = "25",
    val weight: String = "70.0",
    val height: String = "170.0",
    val neck: String = "40.0",
    val waist: String = "80.0",
    val hip: String = "90.0", // Only for females
    val bodyFatPercentage: Double = 0.0,
    val bodyFatCategory: String = ""
)

sealed class BodyFatEvent {
    data class UpdateGender(val gender: Gender) : BodyFatEvent()
    data class UpdateAge(val age: String) : BodyFatEvent()
    data class UpdateWeight(val weight: String) : BodyFatEvent()
    data class UpdateHeight(val height: String) : BodyFatEvent()
    data class UpdateNeck(val neck: String) : BodyFatEvent()
    data class UpdateWaist(val waist: String) : BodyFatEvent()
    data class UpdateHip(val hip: String) : BodyFatEvent()
    object Calculate : BodyFatEvent()
    object Clear : BodyFatEvent()
}

class BodyFatViewModel : ViewModel() {
    var state by mutableStateOf(BodyFatState())
        private set

    fun onEvent(event: BodyFatEvent) {
        when (event) {
            is BodyFatEvent.UpdateGender -> state = state.copy(gender = event.gender)
            is BodyFatEvent.UpdateAge -> state = state.copy(age = event.age)
            is BodyFatEvent.UpdateWeight -> state = state.copy(weight = event.weight)
            is BodyFatEvent.UpdateHeight -> state = state.copy(height = event.height)
            is BodyFatEvent.UpdateNeck -> state = state.copy(neck = event.neck)
            is BodyFatEvent.UpdateWaist -> state = state.copy(waist = event.waist)
            is BodyFatEvent.UpdateHip -> state = state.copy(hip = event.hip)
            is BodyFatEvent.Calculate -> calculateBodyFat()
            is BodyFatEvent.Clear -> state = BodyFatState()
        }
    }

    private fun calculateBodyFat() {
        val height = state.height.toDoubleOrNull() ?: return
        val neck = state.neck.toDoubleOrNull() ?: return
        val waist = state.waist.toDoubleOrNull() ?: return
        
        // US Navy Method
        val bodyFat: Double
        
        if (state.gender == Gender.MALE) {
            // 86.010 * log10(abdomen - neck) - 70.041 * log10(height) + 36.76
            // Note: abdomen is waist
            if (waist <= neck) return // Invalid input
            bodyFat = 86.010 * log10(waist - neck) - 70.041 * log10(height) + 36.76
        } else {
            val hip = state.hip.toDoubleOrNull() ?: return
            // 163.205 * log10(waist + hip - neck) - 97.684 * log10(height) - 78.387
            if (waist + hip <= neck) return // Invalid input
            bodyFat = 163.205 * log10(waist + hip - neck) - 97.684 * log10(height) - 78.387
        }

        val category = getBodyFatCategory(bodyFat, state.gender)

        state = state.copy(
            bodyFatPercentage = bodyFat,
            bodyFatCategory = category
        )
    }

    private fun getBodyFatCategory(percentage: Double, gender: Gender): String {
        return if (gender == Gender.FEMALE) {
            when {
                percentage < 10 -> "Essential Fat"
                percentage < 14 -> "Athletes"
                percentage < 21 -> "Fitness"
                percentage < 25 -> "Average"
                else -> "Obese"
            }
        } else {
            when {
                percentage < 2 -> "Essential Fat"
                percentage < 6 -> "Athletes"
                percentage < 14 -> "Fitness"
                percentage < 18 -> "Average"
                else -> "Obese"
            }
        }
    }
}
