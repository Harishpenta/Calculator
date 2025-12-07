package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

data class SimpleInterestState(
    val principal: Double = 10000.0,
    val rate: Double = 8.0,
    val timeYears: Double = 5.0,
    val interest: Double = 4000.0,
    val totalAmount: Double = 14000.0
)

sealed class SimpleInterestEvent {
    data class UpdatePrincipal(val value: Double) : SimpleInterestEvent()
    data class UpdateRate(val value: Double) : SimpleInterestEvent()
    data class UpdateTime(val value: Double) : SimpleInterestEvent()
}

class SimpleInterestViewModel : ViewModel() {
    var state by mutableStateOf(SimpleInterestState())
        private set

    fun onEvent(event: SimpleInterestEvent) {
        when (event) {
            is SimpleInterestEvent.UpdatePrincipal -> {
                state = state.copy(principal = event.value)
                calculate()
            }
            is SimpleInterestEvent.UpdateRate -> {
                state = state.copy(rate = event.value)
                calculate()
            }
            is SimpleInterestEvent.UpdateTime -> {
                state = state.copy(timeYears = event.value)
                calculate()
            }
        }
    }

    private fun calculate() {
        val p = state.principal
        val r = state.rate
        val t = state.timeYears

        val interest = (p * r * t) / 100
        val total = p + interest

        state = state.copy(
            interest = interest,
            totalAmount = total
        )
    }
}
