package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class UnitCategory {
    Length, Weight, Temperature
}

data class UnitConverterState(
    val category: UnitCategory = UnitCategory.Length,
    val inputValue: Double = 1.0,
    val fromUnit: String = "Meters",
    val toUnit: String = "Feet",
    val resultValue: Double = 0.0
)

class UnitConverterViewModel : ViewModel() {
    var state by mutableStateOf(UnitConverterState())
        private set

    // Conversion factors to Base Unit (Meters, Kilograms, Celsius)
    private val lengthFactors = mapOf(
        "Meters" to 1.0,
        "Kilometers" to 1000.0,
        "Feet" to 0.3048,
        "Miles" to 1609.34,
        "Inches" to 0.0254,
        "Centimeters" to 0.01
    )

    private val weightFactors = mapOf(
        "Kilograms" to 1.0,
        "Grams" to 0.001,
        "Pounds" to 0.453592,
        "Ounces" to 0.0283495
    )

    // Temperature is special, needs formulas
    private val tempUnits = listOf("Celsius", "Fahrenheit", "Kelvin")

    init {
        calculateConversion()
    }

    fun onEvent(event: UnitConverterEvent) {
        when (event) {
            is UnitConverterEvent.UpdateCategory -> {
                val (defaultFrom, defaultTo) = when (event.category) {
                    UnitCategory.Length -> "Meters" to "Feet"
                    UnitCategory.Weight -> "Kilograms" to "Pounds"
                    UnitCategory.Temperature -> "Celsius" to "Fahrenheit"
                }
                state = state.copy(
                    category = event.category,
                    fromUnit = defaultFrom,
                    toUnit = defaultTo
                )
                calculateConversion()
            }
            is UnitConverterEvent.UpdateInput -> {
                state = state.copy(inputValue = event.value)
                calculateConversion()
            }
            is UnitConverterEvent.UpdateFromUnit -> {
                state = state.copy(fromUnit = event.unit)
                calculateConversion()
            }
            is UnitConverterEvent.UpdateToUnit -> {
                state = state.copy(toUnit = event.unit)
                calculateConversion()
            }
            is UnitConverterEvent.SwapUnits -> {
                state = state.copy(
                    fromUnit = state.toUnit,
                    toUnit = state.fromUnit
                )
                calculateConversion()
            }
        }
    }

    private fun calculateConversion() {
        val input = state.inputValue
        val from = state.fromUnit
        val to = state.toUnit

        val result = when (state.category) {
            UnitCategory.Length -> {
                val fromFactor = lengthFactors[from] ?: 1.0
                val toFactor = lengthFactors[to] ?: 1.0
                val baseValue = input * fromFactor // Convert to Meters
                baseValue / toFactor // Convert to Target
            }
            UnitCategory.Weight -> {
                val fromFactor = weightFactors[from] ?: 1.0
                val toFactor = weightFactors[to] ?: 1.0
                val baseValue = input * fromFactor // Convert to Kg
                baseValue / toFactor // Convert to Target
            }
            UnitCategory.Temperature -> convertTemperature(input, from, to)
        }
        
        state = state.copy(resultValue = result)
    }

    private fun convertTemperature(value: Double, from: String, to: String): Double {
        if (from == to) return value
        
        // Convert to Celsius first
        val celsius = when (from) {
            "Fahrenheit" -> (value - 32) * 5 / 9
            "Kelvin" -> value - 273.15
            else -> value // Already Celsius
        }
        
        // Convert from Celsius to Target
        return when (to) {
            "Fahrenheit" -> (celsius * 9 / 5) + 32
            "Kelvin" -> celsius + 273.15
            else -> celsius
        }
    }

    fun getUnitsForCategory(category: UnitCategory): List<String> {
        return when (category) {
            UnitCategory.Length -> lengthFactors.keys.toList()
            UnitCategory.Weight -> weightFactors.keys.toList()
            UnitCategory.Temperature -> tempUnits
        }
    }
}

sealed class UnitConverterEvent {
    data class UpdateCategory(val category: UnitCategory) : UnitConverterEvent()
    data class UpdateInput(val value: Double) : UnitConverterEvent()
    data class UpdateFromUnit(val unit: String) : UnitConverterEvent()
    data class UpdateToUnit(val unit: String) : UnitConverterEvent()
    object SwapUnits : UnitConverterEvent()
}
