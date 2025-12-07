package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.roundToInt

data class TipState(
    val billAmount: Double = 0.0,
    val tipPercentage: Float = 15f,
    val splitCount: Int = 1,
    val tipAmount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val amountPerPerson: Double = 0.0
)

sealed class TipEvent {
    data class UpdateBillAmount(val amount: Double) : TipEvent()
    data class UpdateTipPercentage(val percentage: Float) : TipEvent()
    data class UpdateSplitCount(val count: Int) : TipEvent()
    object Reset : TipEvent()
}

class TipViewModel : ViewModel() {
    var state by mutableStateOf(TipState())
        private set

    fun onEvent(event: TipEvent) {
        when (event) {
            is TipEvent.UpdateBillAmount -> {
                state = state.copy(billAmount = event.amount)
                calculateTip()
            }
            is TipEvent.UpdateTipPercentage -> {
                state = state.copy(tipPercentage = event.percentage)
                calculateTip()
            }
            is TipEvent.UpdateSplitCount -> {
                state = state.copy(splitCount = event.count.coerceAtLeast(1))
                calculateTip()
            }
            is TipEvent.Reset -> {
                state = TipState()
            }
        }
    }

    private fun calculateTip() {
        val bill = state.billAmount
        val tipPercent = state.tipPercentage
        val split = state.splitCount

        if (bill > 0) {
            val tipAmount = bill * (tipPercent / 100)
            val totalAmount = bill + tipAmount
            val perPerson = totalAmount / split

            state = state.copy(
                tipAmount = tipAmount,
                totalAmount = totalAmount,
                amountPerPerson = perPerson
            )
        } else {
            state = state.copy(
                tipAmount = 0.0,
                totalAmount = 0.0,
                amountPerPerson = 0.0
            )
        }
    }
}
