package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class FuelCostState(
    val distance: Double = 0.0,
    val mileage: Double = 0.0,
    val fuelPrice: Double = 0.0,
    val totalCost: Double = 0.0,
    val fuelRequired: Double = 0.0
)

sealed class FuelCostEvent {
    data class UpdateDistance(val distance: Double) : FuelCostEvent()
    data class UpdateMileage(val mileage: Double) : FuelCostEvent()
    data class UpdateFuelPrice(val price: Double) : FuelCostEvent()
    object Reset : FuelCostEvent()
}

class FuelCostViewModel : ViewModel() {
    var state by mutableStateOf(FuelCostState())
        private set

    fun onEvent(event: FuelCostEvent) {
        when (event) {
            is FuelCostEvent.UpdateDistance -> {
                state = state.copy(distance = event.distance)
                calculateCost()
            }
            is FuelCostEvent.UpdateMileage -> {
                state = state.copy(mileage = event.mileage)
                calculateCost()
            }
            is FuelCostEvent.UpdateFuelPrice -> {
                state = state.copy(fuelPrice = event.price)
                calculateCost()
            }
            is FuelCostEvent.Reset -> {
                state = FuelCostState()
            }
        }
    }

    private fun calculateCost() {
        val distance = state.distance
        val mileage = state.mileage
        val price = state.fuelPrice

        if (distance > 0 && mileage > 0 && price > 0) {
            val fuelRequired = distance / mileage
            val totalCost = fuelRequired * price

            state = state.copy(
                fuelRequired = fuelRequired,
                totalCost = totalCost
            )
        } else {
            state = state.copy(
                fuelRequired = 0.0,
                totalCost = 0.0
            )
        }
    }
}
