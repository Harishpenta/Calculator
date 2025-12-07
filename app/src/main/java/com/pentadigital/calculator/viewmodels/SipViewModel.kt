package com.pentadigital.calculator.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlin.math.pow

data class SipState(
    val monthlyInvestment: Double = 5000.0,
    val expectedReturnRate: Double = 12.0,
    val timePeriodYears: Int = 10,
    val investedAmount: Double = 0.0,
    val estimatedReturns: Double = 0.0,
    val totalValue: Double = 0.0
)

class SipViewModel : ViewModel() {
    var state by mutableStateOf(SipState())
        private set

    init {
        calculateSip()
    }

    fun onEvent(event: SipEvent) {
        when (event) {
            is SipEvent.UpdateInvestment -> {
                state = state.copy(monthlyInvestment = event.amount)
                calculateSip()
            }
            is SipEvent.UpdateReturnRate -> {
                state = state.copy(expectedReturnRate = event.rate)
                calculateSip()
            }
            is SipEvent.UpdateTimePeriod -> {
                state = state.copy(timePeriodYears = event.years)
                calculateSip()
            }
        }
    }

    private fun calculateSip() {
        val P = state.monthlyInvestment
        val i = (state.expectedReturnRate / 100) / 12
        val n = state.timePeriodYears * 12

        // M = P × ({[1 + i]^n - 1} / i) × (1 + i)
        val M = P * ((1 + i).pow(n) - 1) / i * (1 + i)
        val invested = P * n
        val returns = M - invested

        state = state.copy(
            investedAmount = invested,
            estimatedReturns = returns,
            totalValue = M
        )
    }
}

sealed class SipEvent {
    data class UpdateInvestment(val amount: Double) : SipEvent()
    data class UpdateReturnRate(val rate: Double) : SipEvent()
    data class UpdateTimePeriod(val years: Int) : SipEvent()
}
