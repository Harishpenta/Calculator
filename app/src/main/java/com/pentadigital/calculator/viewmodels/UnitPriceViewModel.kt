package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class UnitPriceState(
    val priceA: Double = 0.0,
    val quantityA: Double = 0.0,
    val unitA: String = "",
    val priceB: Double = 0.0,
    val quantityB: Double = 0.0,
    val unitB: String = "",
    val unitPriceA: Double = 0.0,
    val unitPriceB: Double = 0.0,
    val verdict: String = "",
    val savings: Double = 0.0,
    val isItemACheaper: Boolean = false,
    val isItemBCheaper: Boolean = false
)

sealed class UnitPriceEvent {
    data class UpdatePriceA(val price: Double) : UnitPriceEvent()
    data class UpdateQuantityA(val quantity: Double) : UnitPriceEvent()
    data class UpdateUnitA(val unit: String) : UnitPriceEvent()
    data class UpdatePriceB(val price: Double) : UnitPriceEvent()
    data class UpdateQuantityB(val quantity: Double) : UnitPriceEvent()
    data class UpdateUnitB(val unit: String) : UnitPriceEvent()
    object Reset : UnitPriceEvent()
}

class UnitPriceViewModel : ViewModel() {
    var state by mutableStateOf(UnitPriceState())
        private set

    fun onEvent(event: UnitPriceEvent) {
        when (event) {
            is UnitPriceEvent.UpdatePriceA -> {
                state = state.copy(priceA = event.price)
                calculate()
            }
            is UnitPriceEvent.UpdateQuantityA -> {
                state = state.copy(quantityA = event.quantity)
                calculate()
            }
            is UnitPriceEvent.UpdateUnitA -> {
                state = state.copy(unitA = event.unit)
            }
            is UnitPriceEvent.UpdatePriceB -> {
                state = state.copy(priceB = event.price)
                calculate()
            }
            is UnitPriceEvent.UpdateQuantityB -> {
                state = state.copy(quantityB = event.quantity)
                calculate()
            }
            is UnitPriceEvent.UpdateUnitB -> {
                state = state.copy(unitB = event.unit)
            }
            is UnitPriceEvent.Reset -> {
                state = UnitPriceState()
            }
        }
    }

    private fun calculate() {
        val priceA = state.priceA
        val qtyA = state.quantityA
        val priceB = state.priceB
        val qtyB = state.quantityB

        var unitPriceA = 0.0
        var unitPriceB = 0.0

        if (qtyA > 0) unitPriceA = priceA / qtyA
        if (qtyB > 0) unitPriceB = priceB / qtyB

        var verdict = ""
        var savings = 0.0
        var isItemACheaper = false
        var isItemBCheaper = false

        if (unitPriceA > 0 && unitPriceB > 0) {
            if (unitPriceA < unitPriceB) {
                isItemACheaper = true
                verdict = "Item A is cheaper"
                savings = ((unitPriceB - unitPriceA) / unitPriceB) * 100
            } else if (unitPriceB < unitPriceA) {
                isItemBCheaper = true
                verdict = "Item B is cheaper"
                savings = ((unitPriceA - unitPriceB) / unitPriceA) * 100
            } else {
                verdict = "Both items have equal value"
            }
        }

        state = state.copy(
            unitPriceA = unitPriceA,
            unitPriceB = unitPriceB,
            verdict = verdict,
            savings = savings,
            isItemACheaper = isItemACheaper,
            isItemBCheaper = isItemBCheaper
        )
    }
}
