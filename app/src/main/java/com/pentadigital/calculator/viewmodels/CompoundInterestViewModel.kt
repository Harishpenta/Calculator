package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.pow

enum class CompoundingFrequency(val timesPerYear: Int) {
    YEARLY(1),
    HALF_YEARLY(2),
    QUARTERLY(4),
    MONTHLY(12)
}

data class CompoundInterestState(
    val principal: Double = 10000.0,
    val rate: Double = 8.0,
    val timeYears: Double = 5.0,
    val frequency: CompoundingFrequency = CompoundingFrequency.YEARLY,
    val interest: Double = 4693.28,
    val totalAmount: Double = 14693.28
)

sealed class CompoundInterestEvent {
    data class UpdatePrincipal(val value: Double) : CompoundInterestEvent()
    data class UpdateRate(val value: Double) : CompoundInterestEvent()
    data class UpdateTime(val value: Double) : CompoundInterestEvent()
    data class UpdateFrequency(val value: CompoundingFrequency) : CompoundInterestEvent()
}

class CompoundInterestViewModel : ViewModel() {
    var state by mutableStateOf(CompoundInterestState())
        private set

    fun onEvent(event: CompoundInterestEvent) {
        when (event) {
            is CompoundInterestEvent.UpdatePrincipal -> {
                state = state.copy(principal = event.value)
                calculate()
            }
            is CompoundInterestEvent.UpdateRate -> {
                state = state.copy(rate = event.value)
                calculate()
            }
            is CompoundInterestEvent.UpdateTime -> {
                state = state.copy(timeYears = event.value)
                calculate()
            }
            is CompoundInterestEvent.UpdateFrequency -> {
                state = state.copy(frequency = event.value)
                calculate()
            }
        }
    }

    private fun calculate() {
        val p = state.principal
        val r = state.rate / 100
        val t = state.timeYears
        val n = state.frequency.timesPerYear.toDouble()

        val amount = p * (1 + r / n).pow(n * t)
        val interest = amount - p

        state = state.copy(
            interest = interest,
            totalAmount = amount
        )
    }
}
